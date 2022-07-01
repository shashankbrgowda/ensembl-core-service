package org.ebi.ensembl.domain.adaptor.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.application.model.Gene;
import org.ebi.ensembl.domain.adaptor.CoreAdaptor;
import org.ebi.ensembl.infra.repo.CoreRepo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class GeneAdaptorTest {
  @InjectMock private CoreRepo<Gene> geneCoreRegistry;

  @Test
  void testFindByDbId() {
    CoreAdaptor<Gene> coreAdaptor = new GeneAdaptor(geneCoreRegistry);

    Gene gene =
        Gene.builder().dbId(1).stableId("ENSCG0001").stableIdVersion(1).isCurrent(true).build();
    Uni<Gene> uni = Uni.createFrom().item(gene);

    when(geneCoreRegistry.findByDbId(any())).thenReturn(uni);
    assertEquals(uni, coreAdaptor.findByDbId(1));
  }
}
