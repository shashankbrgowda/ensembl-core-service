package org.ebi.ensembl.infra.registry.impl;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.domain.model.Gene;
import org.ebi.ensembl.infra.registry.CoreRegistry;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class GeneRegistry implements CoreRegistry<Gene> {
  private final List<Gene> genes;

  public GeneRegistry() {
    Gene gene1 =
        Gene.builder().dbId(1).stableId("ENSCG0001").stableIdVersion(1).isCurrent(true).build();
    Gene gene2 =
        Gene.builder().dbId(2).stableId("ENSCG0002").stableIdVersion(1).isCurrent(true).build();
    this.genes = Arrays.asList(gene1, gene2);
  }

  @Override
  public Uni<Gene> findByDbId(Integer dbId) {
    Optional<Gene> gene =
        this.genes.stream().filter(g -> Objects.equals(dbId, g.getDbId())).findFirst();
    if (gene.isPresent()) {
      return Uni.createFrom().item(gene.get());
    }
    return Uni.createFrom().nullItem();
  }
}
