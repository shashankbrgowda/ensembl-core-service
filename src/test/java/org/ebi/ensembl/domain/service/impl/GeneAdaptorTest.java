package org.ebi.ensembl.domain.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.application.model.GeneObj;
import org.ebi.ensembl.domain.service.CoreService;
import org.ebi.ensembl.grpc.Gene;
import org.ebi.ensembl.infra.repo.CoreRepo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
@Disabled
class GeneAdaptorTest {
  @InjectMock private CoreRepo<Gene> geneCoreRegistry;

  @Test
  void testFindByDbId() {
    CoreService<Gene> coreService = new GeneService(geneCoreRegistry);

//    GeneObj gene =
//        GeneObj.builder().dbId(1).stableId("ENSCG0001").stableIdVersion(1).isCurrent(true).build();
//    Uni<GeneObj> uni = Uni.createFrom().item(gene);
//
//    when(geneCoreRegistry.findByDbId(any(), any())).thenReturn(uni);
//    assertEquals(uni, coreService.fetchByDbId(any(), 1));
  }
}
