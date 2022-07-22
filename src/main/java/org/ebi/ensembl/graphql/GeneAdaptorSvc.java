package org.ebi.ensembl.graphql;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.common.Gene;
import org.ebi.ensembl.grpc.common.RequestMetadata;
import org.ebi.ensembl.repo.GeneRepo;
import org.ebi.ensembl.rest.model.GeneObj;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

// Schema endpoint: http://localhost:8080/graphql/schema.graphql
@GraphQLApi
public class GeneAdaptorSvc {
  private final GeneRepo geneRepo;

  public GeneAdaptorSvc(GeneRepo geneRepo) {
    this.geneRepo = geneRepo;
  }

  // TODO: Jackson doesn't work for protoc generated classes.. need to DTO
  /*@Query("fetchByDbId")
  @Description("Fetch gene by db identifier")
  public Uni<GeneObj> fetchByDbId(
      @Name("dbId") Integer dbId, @Name("requestMetadata") RequestMetadata requestMetadata) {
    return geneRepo.findByDbId(requestMetadata.getConnectionParams(), dbId).onItem().transform(this::mapToGeneObj);
  }*/

  private GeneObj mapToGeneObj(Gene gene) {
    return GeneObj.builder()
        .dbId(gene.getDbId())
        .bioType(gene.getBioType())
        .analysisId(gene.getAnalysisId())
        .seqRegionId(gene.getSeqRegionId())
        .startPos(gene.getStartPos())
        .endPos(gene.getEndPos())
        .strand(gene.getStrand())
        .displayXrefId(gene.getDisplayXrefId())
        .source(gene.getSource())
        .description(gene.getDescription())
        .isCurrent(gene.getIsCurrent())
        .canonicalTranscriptId(gene.getCanonicalTranscriptId())
        .stableId(gene.getStableId())
        .stableIdVersion(gene.getStableIdVersion())
        .createdDate(gene.getCreatedDate())
        .modifiedDate(gene.getModifiedDate())
        .build();
  }
}
