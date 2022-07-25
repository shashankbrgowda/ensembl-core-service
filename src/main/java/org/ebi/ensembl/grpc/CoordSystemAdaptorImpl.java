package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.apache.commons.lang3.StringUtils;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.grpc.common.CoordSystem;
import org.ebi.ensembl.grpc.common.EmptyRequest;
import org.ebi.ensembl.grpc.common.RequestMetadata;
import org.ebi.ensembl.grpc.coord.*;
import org.ebi.ensembl.repo.CoordSystemRepo;
import org.ebi.ensembl.repo.SpeciesRepo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO: Error handling
// TODO: Generic cache of coord system
@GrpcService
public class CoordSystemAdaptorImpl implements CoordSystemAdaptor {
  private final Map<Integer, CoordSystem> RANK_CACHE = new HashMap<>();
  private final Map<String, CoordSystem> NAME_CACHE = new HashMap<>();

  private final CoordSystemRepo coordSystemRepo;
  private final SpeciesRepo speciesRepo;

  public CoordSystemAdaptorImpl(CoordSystemRepo coordSystemRepo, SpeciesRepo speciesRepo) {
    this.coordSystemRepo = coordSystemRepo;
    this.speciesRepo = speciesRepo;
  }

  @Override
  public Uni<FetchAllCoordSystemResponse> fetchAll(EmptyRequest request) {
    ConnectionParams connectionParams = request.getRequestMetadata().getConnectionParams();
    String speciesName = request.getRequestMetadata().getAdaptorMetadata().getSpecies();
    // TODO: Species Id should be passed as metadata from python/Other clients.. Fetch metadata
    // during adaptor creation
    return speciesRepo
        .fetchSpeciesId(connectionParams, speciesName)
        .onItem()
        .transformToMulti(speciesId -> coordSystemRepo.fetchAll(connectionParams, speciesId))
        .collect()
        .asList()
        .onItem()
        .transformToUni(
            coordSystems ->
                Uni.createFrom()
                    .item(
                        FetchAllCoordSystemResponse.newBuilder()
                            .addAllCoordSystems(
                                coordSystems.stream()
                                    .sorted(Comparator.comparingInt(CoordSystem::getRank))
                                    .collect(Collectors.toList()))
                            .build()));
  }

  @Override
  public Uni<CoordSystem> fetchByRank(FetchByCoordSystemRankRequest request) {
    int rank = request.getRank();
    RequestMetadata requestMetadata = request.getRequestMetadata();
    ConnectionParams connectionParams = requestMetadata.getConnectionParams();
    String speciesName = requestMetadata.getAdaptorMetadata().getSpecies();

    if (rank == 0) {
      return fetchTopLevel(EmptyRequest.newBuilder().setRequestMetadata(requestMetadata).build());
    }

    // TODO: Species Id should be passed as metadata from python/Other clients..
    return speciesRepo
        .fetchSpeciesId(connectionParams, speciesName)
        .onItem()
        .transformToUni(
            speciesId -> coordSystemRepo.fetchByRank(connectionParams, speciesId, rank));
  }

  @Override
  public Uni<CoordSystem> fetchByName(FetchByCoordSystemNameRequest request) {
    String name = request.getName();
    String version = request.getVersion();

    EmptyRequest emptyRequest =
        EmptyRequest.newBuilder().setRequestMetadata(request.getRequestMetadata()).build();
    if (StringUtils.equalsIgnoreCase(name, "seqlevel")) {
      return fetchSequenceLevel(emptyRequest);
    } else if (StringUtils.equalsIgnoreCase(name, "toplevel")) {
      return fetchTopLevel(emptyRequest);
    }

    Multi<CoordSystem> coordSystemMulti =
        fetchCoordSystemMulti(emptyRequest)
            .select()
            .where(coordSystem -> StringUtils.equalsIgnoreCase(coordSystem.getName(), name));

    return fetchMatchingCoordSystemFromStream(
        coordSystemMulti,
        coordSystemList -> {
          CoordSystem deafultCoordSystem = null;
          for (CoordSystem coordSystem : coordSystemList) {
            if (StringUtils.equalsIgnoreCase(coordSystem.getVersion(), version)) {
              return Uni.createFrom().item(coordSystem);
            }
            if (coordSystem.getDefault() == 1) {
              deafultCoordSystem = coordSystem;
            }
          }
          if (deafultCoordSystem != null) {
            return Uni.createFrom().item(deafultCoordSystem);
          }
          // didn't find a default, just take first one
          if (!coordSystemList.isEmpty()) {
            return Uni.createFrom().item(coordSystemList.get(0));
          }
          return Uni.createFrom().nullItem();
        });
  }

  private Uni<CoordSystem> fetchMatchingCoordSystemFromStream(
      Multi<CoordSystem> multi,
      Function<? super List<CoordSystem>, Uni<? extends CoordSystem>> function) {
    return multi.collect().asList().onItem().transformToUni(function);
  }

  private Multi<CoordSystem> fetchCoordSystemMulti(EmptyRequest request) {
    return fetchAll(request)
        .onItem()
        .transformToMulti(
            fetchAllCoordSystemResponse ->
                Multi.createFrom()
                    .items(fetchAllCoordSystemResponse.getCoordSystemsList().stream()));
  }

  @Override
  public Uni<CoordSystem> fetchSequenceLevel(EmptyRequest request) {
    Multi<CoordSystem> coordSystemMulti = fetchCoordSystemMulti(request);
    return fetchMatchingCoordSystemFromStream(
        coordSystemMulti,
        coordSystemList -> {
          int sequenceLevelCount = 0;
          CoordSystem coordSystem = null;
          for (CoordSystem cs : coordSystemList) {
            if (cs.getSequenceLevel() == 1) {
              sequenceLevelCount += 1;
              coordSystem = cs;
            }
          }
          if (sequenceLevelCount > 1) {
            return Uni.createFrom().nullItem();
          }
          return Uni.createFrom().item(coordSystem);
        });
  }

  @Override
  public Uni<CoordSystem> fetchTopLevel(EmptyRequest request) {
    return Uni.createFrom()
        .item(CoordSystem.newBuilder().setName("toplevel").setTopLevel(1).build());
  }
}
