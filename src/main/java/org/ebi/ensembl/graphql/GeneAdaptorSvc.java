package org.ebi.ensembl.graphql;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.rest.model.GeneObj;
import org.ebi.ensembl.grpc.Gene;
import org.ebi.ensembl.repo.CoreRepo;
import org.ebi.ensembl.repo.handler.ConnectionParams;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

// Schema endpoint: http://localhost:8080/graphql/schema.graphql
@GraphQLApi
public class GeneAdaptorSvc {
  private final CoreRepo<Gene> geneCoreRepo;

  public GeneAdaptorSvc(CoreRepo<Gene> geneCoreRepo) {
    this.geneCoreRepo = geneCoreRepo;
  }

  @Query("fetchByDbId")
  @Description("Fetch gene by db identifier")
  public Uni<GeneObj> fetchByDbId(
      @Name("dbId") Integer dbId, @Name("connectionParams") ConnectionParams connectionParams) {
    return geneCoreRepo.findByDbId(connectionParams, dbId).onItem().transform(this::mapToGeneObj);
  }

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