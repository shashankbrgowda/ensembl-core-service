package org.ebi.ensembl.infra.registry;

import io.smallrye.mutiny.Uni;

public interface CoreRegistry<T> {
    Uni<T> findByDbId(Integer dbId);
}
