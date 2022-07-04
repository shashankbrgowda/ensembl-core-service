package org.ebi.ensembl.domain.adaptor;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.infra.repo.handler.ConnectionParams;

public interface CoreAdaptor<T> {
  Uni<T> fetchByDbId(ConnectionParams params, Integer dbId);
}
