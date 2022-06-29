package org.ebi.ensembl.domain.adaptor.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.domain.adaptor.CoreAdaptor;
import org.ebi.ensembl.domain.model.Gene;
import org.ebi.ensembl.infra.registry.CoreRegistry;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class GeneAdaptorTest {

  @Inject private CoreAdaptor<Gene> coreAdaptor;

  @InjectMock private CoreRegistry<Gene> geneCoreRegistry;

  @Test
  void testFindByDbId() {
    Gene gene =
        Gene.builder().dbId(1).stableId("ENSCG0001").stableIdVersion(1).isCurrent(true).build();
    Uni<Gene> uni = Uni.createFrom().item(gene);

    when(geneCoreRegistry.findByDbId(any())).thenReturn(uni);
    assertEquals(uni, coreAdaptor.findByDbId(1));
  }
}
