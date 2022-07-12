package org.ebi.ensembl.repo;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.repo.handler.ConnectionParams;

public interface CoreRepo<T> {
  Uni<T> findByDbId(ConnectionParams params, Integer dbId);
}
