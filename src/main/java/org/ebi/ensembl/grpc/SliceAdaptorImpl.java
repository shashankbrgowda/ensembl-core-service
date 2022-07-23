package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.grpc.common.RequestMetadata;
import org.ebi.ensembl.grpc.common.Slice;
import org.ebi.ensembl.grpc.coord.CoordSystemAdaptor;
import org.ebi.ensembl.grpc.coord.FetchByNameRequest;
import org.ebi.ensembl.grpc.slice.FetchAllSliceRequest;
import org.ebi.ensembl.grpc.slice.FetchAllSliceResponse;
import org.ebi.ensembl.grpc.slice.SliceAdaptor;
import org.ebi.ensembl.repo.SequenceRegionRepo;
import org.ebi.ensembl.repo.SpeciesRepo;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@GrpcService
public class SliceAdaptorImpl implements SliceAdaptor {
  @GrpcClient CoordSystemAdaptor coordSystemAdaptor;

  @Inject SequenceRegionRepo sequenceRegionRepo;

  @Inject SpeciesRepo speciesRepo;

  @Override
  public Uni<FetchAllSliceResponse> fetchAll(FetchAllSliceRequest request) {
    String coordSystemName = request.getCoordSystemName();
    String coordSystemVersion = request.getCoordSystemVersion();
    boolean includeNonRef = request.getIncludeNonReference();
    boolean includeLrg = request.getIncludeLrg();
    // TODO: include duplicates argument, assembly exception table.. include duplicates
    int includeDuplicates = request.getIncludeDuplicates();

    RequestMetadata requestMetadata = request.getRequestMetadata();
    ConnectionParams connectionParams = requestMetadata.getConnectionParams();
    String speciesName = requestMetadata.getAdaptorMetadata().getSpecies();
    FetchByNameRequest fetchByNameRequest =
        FetchByNameRequest.newBuilder()
            .setRequestMetadata(requestMetadata)
            .setName(coordSystemName)
            .setVersion(coordSystemVersion)
            .build();

    AtomicReference<String> sql =
        new AtomicReference<>(
            "SELECT sr.seq_region_id, sr.name as sr_name, sr.length, sr.coord_system_id, "
                + "cs.name cs_name, cs.rank, cs.version, cs.attrib FROM seq_region sr, seq_region_attrib sra, "
                + "attrib_type at, coord_system cs WHERE at.code = '%s' AND sra.seq_region_id = sr.seq_region_id AND "
                + "at.attrib_type_id = sra.attrib_type_id AND sr.coord_system_id = cs.coord_system_id AND cs.species_id = %d");

    return speciesRepo
        .fetchSpeciesId(connectionParams, speciesName)
        .onItem()
        .transformToUni(
            speciesId ->
                mergeAndFilterLrgNonRefSlices(
                    speciesId,
                    connectionParams,
                    includeNonRef,
                    includeLrg,
                    sql,
                    fetchByNameRequest));
  }

  private Uni<FetchAllSliceResponse> mergeAndFilterLrgNonRefSlices(
      Integer speciesId,
      ConnectionParams connectionParams,
      boolean includeNonRef,
      boolean includeLrg,
      AtomicReference<String> sql,
      FetchByNameRequest fetchByNameRequest) {
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
                            String sqlStr =
                                "SELECT sr.seq_region_id, sr.name as sr_name, sr.length, sr.coord_system_id, "
                                    + "cs.name as cs_name, cs.rank, cs.version, cs.attrib FROM seq_region sr, coord_system cs "
                                    + "WHERE sr.coord_system_id = cs.coord_system_id AND cs.coord_system_id = %d";
                            sliceMulti =
                                sequenceRegionRepo.genericFetch(
                                    connectionParams, String.format(sqlStr, coordSystem.getDbId()));
                          }
                          return sliceMulti
                              .select()
                              .where(
                                  slice -> !lrgNonRefSlicesMap.containsKey(slice.getSeqRegionId()))
                              .collect()
                              .asList();
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
    return Multi.createBy()
        .combining()
        .streams(sliceMulti1, sliceMulti2)
        .asTuple()
        .onItem()
        .transformToMultiAndMerge(
            tuple2 -> Multi.createFrom().items(tuple2.getItem1(), tuple2.getItem2()));
  }

  private Uni<Map<Integer, Slice>> slicesAsMap(Multi<Slice> lrgNonRefSlices) {
    return lrgNonRefSlices.collect().asMap(Slice::getSeqRegionId, slice -> slice, (s1, s2) -> s1);
  }

  private Function<? super List<Slice>, Uni<? extends FetchAllSliceResponse>> sliceResponseFunc =
      slices ->
          Uni.createFrom().item(FetchAllSliceResponse.newBuilder().addAllSlices(slices).build());
}
