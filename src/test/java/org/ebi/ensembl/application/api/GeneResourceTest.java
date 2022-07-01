package org.ebi.ensembl.application.api;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.application.model.Gene;
import org.ebi.ensembl.domain.adaptor.CoreAdaptor;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class GeneResourceTest {

  @InjectMock private CoreAdaptor<Gene> coreAdaptor;

  @Test
  void testGetGene() {
    Gene gene =
        Gene.builder().dbId(1).stableId("ENSCG0001").stableIdVersion(1).isCurrent(true).build();
    Uni<Gene> uni = Uni.createFrom().item(gene);

    when(coreAdaptor.findByDbId(any())).thenReturn(uni);

    given()
        .when()
        .pathParam("dbId", 1)
        .get("api/genes/{dbId}")
        .then()
        .statusCode(200)
        .body("dbId", equalTo(1))
        .body("stableId", equalTo("ENSCG0001"));
  }
}
