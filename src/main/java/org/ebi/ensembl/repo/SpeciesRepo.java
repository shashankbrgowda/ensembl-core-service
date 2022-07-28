package org.ebi.ensembl.repo;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.handler.ConnectionHandler;

import javax.enterprise.context.ApplicationScoped;

import static org.ebi.ensembl.util.DbUtil.protoDefaultValue;

// TODO: Error handling
@ApplicationScoped
public class SpeciesRepo {
  private final ConnectionHandler connectionHandler;

  public SpeciesRepo(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
  }

  public Uni<Integer> fetchSpeciesId(ConnectionParams params, String speciesName) {
    return connectionHandler
        .pool(params)
        .query(
            "select distinct species_id from meta where meta_key='species.alias' and meta_value like '%"
                + speciesName
                + "%'")
        .execute()
        .onItem()
        .transform(RowSet::iterator)
        .onItem()
        .transform(
            itr -> {
              if (itr.hasNext()) {
                Row row = itr.next();
                return protoDefaultValue(row.getInteger("species_id"), Integer.class);
              }
              return null;
            });
  }
}
