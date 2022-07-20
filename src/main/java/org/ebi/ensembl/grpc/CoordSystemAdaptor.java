package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.common.CoordSystem;
import org.ebi.ensembl.grpc.coord.*;
import org.ebi.ensembl.repo.CoordSystemRepo;
import org.ebi.ensembl.repo.SpeciesRepo;
import org.ebi.ensembl.handler.ConnectionParams;
import java.util.HashMap;
import java.util.Map;

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
    return null;
  }

  @Override
  public Uni<CoordSystem> fetchByName(FetchByNameRequest request) {
    return null;
  }

  @Override
  public Uni<CoordSystem> fetchSequenceLevel(EmptyRequest request) {
    return null;
  }

  @Override
  public Uni<CoordSystem> fetchTopLevel(EmptyRequest request) {
    return null;
  }
}
