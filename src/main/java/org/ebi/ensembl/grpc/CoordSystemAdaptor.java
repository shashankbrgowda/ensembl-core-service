package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ebi.ensembl.grpc.common.CoordSystem;
import org.ebi.ensembl.grpc.coord.*;
import org.ebi.ensembl.repo.CoordSystemRepo;
import org.ebi.ensembl.repo.SpeciesRepo;
import org.ebi.ensembl.handler.ConnectionParams;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@GrpcService
public class CoordSystemAdaptor implements CoordSystemSvc {
  private final Map<Integer, CoordSystem> RANK_CACHE = new HashMap<>();
  private final Map<String, CoordSystem> NAME_CACHE = new HashMap<>();

  private final CoordSystemRepo coordSystemRepo;
  private final SpeciesRepo speciesRepo;

  public CoordSystemAdaptor(CoordSystemRepo coordSystemRepo, SpeciesRepo speciesRepo) {
    this.coordSystemRepo = coordSystemRepo;
    this.speciesRepo = speciesRepo;
  }

  @Override
  public Multi<CoordSystem> fetchAll(EmptyRequest request) {
    ConnectionParams connectionParams = ConnectionParams.mapConnectionParams(request.getParams());
    String speciesName = connectionParams.getRequestMetadata().getSpecies();
    return speciesRepo
        .fetchSpeciesId(connectionParams, speciesName)
        .onItem()
        .transformToMulti(
            speciesId -> coordSystemRepo.fetchAll(connectionParams, speciesId));
  }

  @Override
  public Uni<CoordSystem> fetchByRank(FetchByRankRequest request) {
    int rank = request.getRank();
    ConnectionParams connectionParams = ConnectionParams.mapConnectionParams(request.getParams());
    String speciesName = connectionParams.getRequestMetadata().getSpecies();

    if (rank == 0) {
      return fetchTopLevel(EmptyRequest.newBuilder().setParams(request.getParams()).build());
    }

    return speciesRepo
        .fetchSpeciesId(connectionParams, speciesName)
        .onItem()
        .transformToUni(
            speciesId -> coordSystemRepo.fetchByRank(connectionParams, speciesId, rank));
  }

  @Override
  public Uni<CoordSystem> fetchByName(FetchByNameRequest request) {
    String name = request.getName().toLowerCase();
    String version = request.getVersion();

    EmptyRequest emptyRequest = EmptyRequest.newBuilder().setParams(request.getParams()).build();
    if (Objects.equals(name, "seqlevel")) {
      return fetchSequenceLevel(emptyRequest);
    } else if (Objects.equals(name, "toplevel")) {
      return fetchTopLevel(emptyRequest);
    }

    return fetchAll(emptyRequest)
        .select()
        .where(coordSystem -> StringUtils.equalsIgnoreCase(coordSystem.getName(), name))
        .collect()
        .asList()
        .onItem()
        .transformToUni(
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
              return Uni.createFrom().item(coordSystemList.get(0));
            });
  }

  @Override
  public Uni<CoordSystem> fetchSequenceLevel(EmptyRequest request) {
    return fetchAll(request)
            .collect()
            .asList()
            .onItem()
            .transformToUni(coordSystemList -> {
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
