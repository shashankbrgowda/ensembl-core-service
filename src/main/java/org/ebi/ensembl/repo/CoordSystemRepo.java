package org.ebi.ensembl.repo;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.ebi.ensembl.grpc.common.CoordSystem;
import org.ebi.ensembl.handler.ConnectionHandler;
import org.ebi.ensembl.handler.ConnectionParams;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class CoordSystemRepo {
  private final ConnectionHandler connectionHandler;

  public CoordSystemRepo(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
  }

  public Multi<CoordSystem> fetchAll(ConnectionParams connectionParams, Integer speciesId) {
    return connectionHandler
        .pool(connectionParams)
        .preparedQuery(
            "select coord_system_id, name, rank, version, attrib from coord_system where species_id = ?")
        .execute(Tuple.of(speciesId))
        .onItem()
        .transformToMulti(set -> Multi.createFrom().iterable(set))
        .onItem()
        .transform(this::mapCoordSystem);
  }

  private CoordSystem mapCoordSystem(Row r) {
    CoordSystem.Builder coordSysBuilder = CoordSystem.newBuilder();

    if (Objects.isNull(r)) {
      return null;
    }

    if (Objects.nonNull(r.getString("attrib"))) {
      String[] attribs = r.getString("attrib").split(",");
      for (String attrib : attribs) {
        if (Objects.equals(attrib, "sequence_level")) {
          coordSysBuilder.setDefault(1);
        }
        if (Objects.equals(attrib, "default_version")) {
          coordSysBuilder.setSequenceLevel(1);
        }
      }
    }

    if (Objects.nonNull(r.getInteger("coord_system_id"))) {
      coordSysBuilder.setDbId(r.getInteger("coord_system_id"));
    }

    if (Objects.nonNull(r.getString("name"))) {
      coordSysBuilder.setName(r.getString("name"));
    }

    if (Objects.nonNull(r.getInteger("rank"))) {
      coordSysBuilder.setRank(r.getInteger("rank"));
    }

    if (Objects.nonNull(r.getString("version"))) {
      coordSysBuilder.setVersion(r.getString("version"));
    }

    return coordSysBuilder.build();
  }
}
