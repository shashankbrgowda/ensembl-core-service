package org.ebi.ensembl.rest.resource;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.common.Gene;
import org.ebi.ensembl.repo.GeneRepo;
import org.ebi.ensembl.rest.model.GeneObj;
import org.ebi.ensembl.handler.ConnectionParams;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

@Path("genes")
public class GeneResource {
  private final GeneRepo geneRepo;

  public GeneResource(GeneRepo geneRepo) {
    this.geneRepo = geneRepo;
  }

  // TODO: Jackson doesn't work for protoc generated classes.. need to DTO
  @POST
  @Path("{dbId}")
  public Uni<GeneObj> gene(@PathParam("dbId") Integer dbId, ConnectionParams connectionParams) {
    return geneRepo.findByDbId(connectionParams, dbId).onItem().transform(this::mapToGeneObj);
  }

  @GET
  @Path("download")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response download() throws IOException {
    File file = new File("/Users/shabr/Documents/ENCSR879GEQ.merged.bam.zip");
    return Response.ok(file)
        .header("Content-Disposition", "attachment;filename=" + file.getName())
        .build();
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
