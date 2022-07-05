package org.ebi.ensembl.application.api;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.application.model.GeneObj;
import org.ebi.ensembl.domain.service.CoreService;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class GeneResourceTest {

  @InjectMock private CoreService<GeneObj> coreService;

  @Test
  void testGetGene() {
    GeneObj gene =
        GeneObj.builder().dbId(1).stableId("ENSCG0001").stableIdVersion(1).isCurrent(true).build();
    Uni<GeneObj> uni = Uni.createFrom().item(gene);

    when(coreService.fetchByDbId(any(), any())).thenReturn(uni);

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
