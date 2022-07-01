package org.ebi.ensembl.infra.repo;

import io.smallrye.mutiny.Uni;

public interface CoreRepo<T> {
  Uni<T> findByDbId(Integer dbId);
}
