package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.application.model.GeneObj;
import org.ebi.ensembl.domain.service.CoreService;
import org.ebi.ensembl.infra.repo.handler.ConnectionParams;

import java.util.Objects;

@GrpcService
public class GeneAdaptorImpl implements GeneAdaptor {
  private final CoreService<GeneObj> geneCoreService;

  public GeneAdaptorImpl(CoreService<GeneObj> geneCoreService) {
    this.geneCoreService = geneCoreService;
  }

  @Override
  public Uni<Gene> fetchByDbId(FetchByDbIdRequest request) {
    return geneCoreService
        .fetchByDbId(mapConnectionParams(request.getParams()), request.getDbId())
        .onItem()
        .transform(this::mapToGene);
  }

  private ConnectionParams mapConnectionParams(
      org.ebi.ensembl.grpc.ConnectionParams connectionParams) {
    return ConnectionParams.builder()
        .host(connectionParams.getHost())
        .port(connectionParams.getPort())
        .dbName(connectionParams.getDbName())
        .userName(connectionParams.getUserName())
        .password(connectionParams.getPassword())
        .build();
  }

  private Gene mapToGene(GeneObj geneObj) {
    Gene.Builder geneBuilder = Gene.newBuilder();

    if (Objects.isNull(geneObj)) {
      return null;
    }

    if (geneObj.getDbId() != null) {
      geneBuilder.setDbId(geneObj.getDbId());
    }

    if (geneObj.getStableId() != null) {
      geneBuilder.setStableId(geneObj.getStableId());
    }

    if (geneObj.getStableIdVersion() != null) {
      geneBuilder.setStableIdVersion(geneObj.getStableIdVersion());
    }

    if (geneObj.getSeqRegionId() != null) {
      geneBuilder.setSeqRegionId(geneObj.getSeqRegionId());
    }

    if (geneObj.getStartPos() != null) {
      geneBuilder.setStartPos(geneObj.getStartPos());
    }

    if (geneObj.getEndPos() != null) {
      geneBuilder.setEndPos(geneObj.getEndPos());
    }

    if (geneObj.getStrand() != null) {
      geneBuilder.setStrand(geneObj.getStrand());
    }

    if (geneObj.getBioType() != null) {
      geneBuilder.setBioType(geneObj.getBioType());
    }

    if (geneObj.getDescription() != null) {
      geneBuilder.setDescription(geneObj.getDescription());
    }

    if (geneObj.getSource() != null) {
      geneBuilder.setSource(geneObj.getSource());
    }

    if (geneObj.getAnalysisId() != null) {
      geneBuilder.setAnalysisId(geneObj.getAnalysisId());
    }

    if (geneObj.getIsCurrent() != null) {
      geneBuilder.setIsCurrent(geneObj.getIsCurrent());
    }

    if (geneObj.getDisplayXrefId() != null) {
      geneBuilder.setDisplayXrefId(geneObj.getDisplayXrefId());
    }

    if (geneObj.getExternalName() != null) {
      geneBuilder.setExternalName(geneObj.getExternalName());
    }

    if (geneObj.getExternalDb() != null) {
      geneBuilder.setExternalDb(geneObj.getExternalDb());
    }

    if (geneObj.getExternalStatus() != null) {
      geneBuilder.setExternalStatus(geneObj.getExternalStatus());
    }

    if (geneObj.getCanonicalTranscriptId() != null) {
      geneBuilder.setCanonicalTranscriptId(geneObj.getCanonicalTranscriptId());
    }

    if (geneObj.getCreatedDate() != null) {
      geneBuilder.setCreatedDate(geneObj.getCreatedDate().toString());
    }

    if (geneObj.getModifiedDate() != null) {
      geneBuilder.setModifiedDate(geneObj.getModifiedDate().toString());
    }

    return geneBuilder.build();
  }
}
