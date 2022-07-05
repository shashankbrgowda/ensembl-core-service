package org.ebi.ensembl.domain.service;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.infra.repo.handler.ConnectionParams;

public interface CoreService<T> {
  Uni<T> fetchByDbId(ConnectionParams params, Integer dbId);
}
