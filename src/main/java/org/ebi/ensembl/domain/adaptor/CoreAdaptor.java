package org.ebi.ensembl.domain.adaptor;

import io.smallrye.mutiny.Uni;

public interface CoreAdaptor<T> {
  Uni<T> findByDbId(Integer dbId);
}
