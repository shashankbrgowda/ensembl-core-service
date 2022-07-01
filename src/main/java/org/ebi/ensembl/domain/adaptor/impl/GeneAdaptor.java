package org.ebi.ensembl.domain.adaptor.impl;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.application.model.Gene;
import org.ebi.ensembl.domain.adaptor.CoreAdaptor;
import org.ebi.ensembl.infra.repo.CoreRepo;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneAdaptor implements CoreAdaptor<Gene> {
  private final CoreRepo<Gene> geneCoreRepo;

  public GeneAdaptor(CoreRepo<Gene> geneCoreAdaptor) {
    this.geneCoreRepo = geneCoreAdaptor;
  }

  @Override
  public Uni<Gene> findByDbId(Integer dbId) {
    return geneCoreRepo.findByDbId(dbId);
  }
}
