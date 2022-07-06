package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.infra.repo.CoreRepo;
import org.ebi.ensembl.infra.repo.handler.ConnectionParams;

@GrpcService
public class GeneAdaptorImpl implements GeneAdaptor {
  private final CoreRepo<Gene> geneCoreRepo;

  public GeneAdaptorImpl(CoreRepo<Gene> geneCoreRepo) {
    this.geneCoreRepo = geneCoreRepo;
  }

  /**
   * Returns the feature created from the database defined by the the id $id. The feature will be
   * returned in its native coordinate system. That is, the coordinate system in which it is stored
   * in the database. In order to convert it to a particular coordinate system use the transfer() or
   * transform() method. If the feature is not found in the database then undef is returned instead
   *
   * @param request Db connection parameters and dbId (The unique database identifier for the
   *     feature to be obtained)
   */
  @Override
  public Uni<Gene> fetchByDbId(FetchByDbIdRequest request) {
    return geneCoreRepo.findByDbId(mapConnectionParams(request.getParams()), request.getDbId());
  }

  private ConnectionParams mapConnectionParams(
      org.ebi.ensembl.grpc.ConnectionParams connectionParams) {
    return ConnectionParams.builder()
        .host(connectionParams.getHost())
        .port(connectionParams.getPort())
        .dbName(connectionParams.getDbName())
        .userName(connectionParams.getUserName())
        .password(connectionParams.getPassword())
        .build();
  }
}
