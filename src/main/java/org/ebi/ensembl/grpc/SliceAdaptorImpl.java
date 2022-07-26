package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.grpc.common.RequestMetadata;
import org.ebi.ensembl.grpc.common.Slice;
import org.ebi.ensembl.grpc.coord.CoordSystemAdaptor;
import org.ebi.ensembl.grpc.coord.FetchByCoordSystemNameRequest;
import org.ebi.ensembl.grpc.slice.*;
import org.ebi.ensembl.repo.SequenceRegionRepo;
import org.ebi.ensembl.repo.SpeciesRepo;
import org.ebi.ensembl.util.MutinyUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

// TODO: Error handling
@GrpcService
public class SliceAdaptorImpl implements SliceAdaptor {
  @GrpcClient CoordSystemAdaptor coordSystemAdaptor;

  @Inject SequenceRegionRepo sequenceRegionRepo;

  @Inject SpeciesRepo speciesRepo;

  // TODO: assembly exception table..
  // TODO: Move SQL to repo layer..
  @Override
  public Uni<FetchAllSliceResponse> fetchAll(FetchAllSliceRequest request) {
    RequestMetadata requestMetadata = request.getRequestMetadata();
    FetchByCoordSystemNameRequest fetchByNameRequest =
        FetchByCoordSystemNameRequest.newBuilder()
            .setRequestMetadata(requestMetadata)
            .setName(request.getCoordSystemName())
            .setVersion(request.getCoordSystemVersion())
            .build();
    AtomicReference<String> sql =
        new AtomicReference<>("""
                SELECT sr.seq_region_id, sr.name as sr_name, sr.length, sr.coord_system_id, cs.name as cs_name, cs.rank, 
                cs.version, cs.attrib FROM seq_region sr, seq_region_attrib sra, attrib_type at, coord_system cs 
                WHERE at.code = '%s' AND sra.seq_region_id = sr.seq_region_id AND at.attrib_type_id = sra.attrib_type_id 
                AND sr.coord_system_id = cs.coord_system_id AND cs.species_id = %d""");

    return speciesRepo
        .fetchSpeciesId(
            requestMetadata.getConnectionParams(),
            requestMetadata.getAdaptorMetadata().getSpecies())
        .onItem()
        .transformToUni(
            speciesId ->
                mergeAndFilterLrgNonRefSlices(
                    speciesId,
                    requestMetadata.getConnectionParams(),
                    request.getIncludeNonReference(),
                    request.getIncludeLrg(),
                    sql,
                    fetchByNameRequest));
  }

  // TODO: Circular slice, Caching, Karyotype stuff
  @Override
  public Uni<Slice> fetchByRegion(FetchBySliceRegionRequest request) {
    int start = request.getStart() != 0 ? request.getStart() : 1;
    int strand = request.getStrand() != 0 ? request.getStrand() : 1;
    int end = request.getEnd();

    if (!request.getCoordSystemName().isEmpty()) {
      FetchByCoordSystemNameRequest fetchByNameRequest =
          FetchByCoordSystemNameRequest.newBuilder()
              .setRequestMetadata(request.getRequestMetadata())
              .setName(request.getCoordSystemName())
              .setVersion(request.getVersion())
              .build();
      return coordSystemAdaptor
          .fetchByName(fetchByNameRequest)
          .onItem()
          .transformToUni(
              cs ->
                  sequenceRegionRepo
                      .fetchSeqRegionByNameAndCoordSysId(
                          request.getRequestMetadata().getConnectionParams(),
                          request.getSeqRegionName(),
                          cs.getDbId())
                      .onItem()
                      .transform(
                          slice -> {
                            if (slice == null) {
                              return null;
                            }
                            Slice.Builder builder = slice.toBuilder();
                            builder.setCoordSystem(cs);
                            if (start != 1) {
                              builder.setStart(start);
                            }
                            if (strand != 1) {
                              builder.setStrand(strand);
                            }
                            if (end != 0) {
                              builder.setEnd(end);
                            }
                            return builder.build();
                          }));
    }

    return Uni.createFrom().nullItem();
  }

  @Override
  public Uni<Slice> fetchByName(FetchBySliceNameRequest request) {
    String sliceName = request.getName();
    String[] args = sliceName.split(":");

    // TODO: throw error
    if (args.length != 6) {
      return Uni.createFrom().nullItem();
    }

    FetchBySliceRegionRequest fetchBySliceRegionRequest =
        FetchBySliceRegionRequest.newBuilder()
            .setRequestMetadata(request.getRequestMetadata())
            .setCoordSystemName(args[0])
            .setVersion(args[1])
            .setSeqRegionName(args[2])
            .setStart(Integer.parseInt(args[3]))
            .setEnd(Integer.parseInt(args[4]))
            .setStrand(Integer.parseInt(args[5]))
            .build();

    return fetchByRegion(fetchBySliceRegionRequest);
  }

  @Override
  public Uni<Slice> fetchBySeqRegionId(FetchBySeqRegionIdRequest request) {
    return sequenceRegionRepo
        .fetchBySeqRegionId(
            request.getRequestMetadata().getConnectionParams(), request.getSeqRegionId())
        .onItem()
        .transform(
            slice -> {
              Slice.Builder builder = slice.toBuilder();
              if (request.getStart() != 0) {
                builder.setStart(request.getStart());
              }
              if (request.getEnd() != 0) {
                builder.setEnd(request.getEnd());
              }
              if (request.getStrand() != 0) {
                builder.setStrand(request.getStrand());
              }
              return builder.build();
            });
  }

  private Uni<FetchAllSliceResponse> mergeAndFilterLrgNonRefSlices(
      Integer speciesId,
      ConnectionParams connectionParams,
      boolean includeNonRef,
      boolean includeLrg,
      AtomicReference<String> sql,
      FetchByCoordSystemNameRequest fetchByNameRequest) {
    Multi<Slice> lrgNonRefSlices =
        lrgNonRefSlices(connectionParams, speciesId, includeLrg, includeNonRef, sql);
    return coordSystemAdaptor
        .fetchByName(fetchByNameRequest)
        .onItem()
        .transformToUni(
            coordSystem ->
                slicesAsMap(lrgNonRefSlices)
                    .onItem()
                    .transformToUni(
                        lrgNonRefSlicesMap -> {
                          Multi<Slice> sliceMulti;
                          if (coordSystem.getTopLevel() == 1) {
                            sliceMulti =
                                sequenceRegionRepo.genericFetch(
                                    connectionParams,
                                    String.format(sql.get(), "toplevel", speciesId));
                          } else {
                            String sqlStr = """
                                    SELECT sr.seq_region_id, sr.name as sr_name, sr.length, sr.coord_system_id,
                                    cs.name as cs_name, cs.rank, cs.version, cs.attrib FROM seq_region sr, coord_system cs
                                    WHERE sr.coord_system_id = cs.coord_system_id AND sr.coord_system_id = %d""";
                            sliceMulti =
                                sequenceRegionRepo.genericFetch(
                                    connectionParams, String.format(sqlStr, coordSystem.getDbId()));
                          }
                          return MutinyUtil.filterAndCollectMultiAsList(
                              sliceMulti,
                              slice -> !lrgNonRefSlicesMap.containsKey(slice.getSeqRegionId()));
                        })
                    .onItem()
                    .transformToUni(sliceResponseFunc));
  }

  private Multi<Slice> lrgNonRefSlices(
      ConnectionParams connectionParams,
      Integer speciesId,
      boolean includeNonRef,
      boolean includeLrg,
      AtomicReference<String> sql) {
    Multi<Slice> sliceMulti1 = Multi.createFrom().empty();
    Multi<Slice> sliceMulti2 = Multi.createFrom().empty();
    if (!includeNonRef) {
      sliceMulti1 =
          sequenceRegionRepo.genericFetch(
              connectionParams, String.format(sql.get(), "non_ref", speciesId));
    }
    if (!includeLrg) {
      sliceMulti2 =
          sequenceRegionRepo.genericFetch(
              connectionParams, String.format(sql.get(), "LRG", speciesId));
    }
    return MutinyUtil.combineMultiAndMerge(sliceMulti1, sliceMulti2);
  }

  private Uni<Map<Integer, Slice>> slicesAsMap(Multi<Slice> lrgNonRefSlices) {
    return lrgNonRefSlices.collect().asMap(Slice::getSeqRegionId, slice -> slice, (s1, s2) -> s1);
  }

  private Function<? super List<Slice>, Uni<? extends FetchAllSliceResponse>> sliceResponseFunc =
      slices ->
          Uni.createFrom().item(FetchAllSliceResponse.newBuilder().addAllSlices(slices).build());
}
