package org.ebi.ensembl.infra.repo;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.infra.repo.handler.ConnectionParams;

public interface CoreRepo<T> {
  Uni<T> findByDbId(ConnectionParams params, Integer dbId);
}
