package org.ebi.ensembl.domain.adaptor.impl;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.domain.adaptor.CoreAdaptor;
import org.ebi.ensembl.domain.model.Gene;
import org.ebi.ensembl.infra.registry.CoreRegistry;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneAdaptor implements CoreAdaptor<Gene> {
  private final CoreRegistry<Gene> geneCoreRegistry;

  public GeneAdaptor(CoreRegistry<Gene> geneCoreAdaptor) {
    this.geneCoreRegistry = geneCoreAdaptor;
  }

  @Override
  public Uni<Gene> findByDbId(Integer dbId) {
    return geneCoreRegistry.findByDbId(dbId);
  }
}
