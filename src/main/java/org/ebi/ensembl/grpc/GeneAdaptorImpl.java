package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.common.Gene;
import org.ebi.ensembl.grpc.gene.*;
import org.ebi.ensembl.repo.GeneRepo;

@GrpcService
public class GeneAdaptorImpl implements GeneAdaptor {
  private final GeneRepo geneRepo;

  public GeneAdaptorImpl(GeneRepo geneRepo) {
    this.geneRepo = geneRepo;
  }

  @Override
  public Uni<CountResponse> countAllByBioTypes(CountAllByBioTypesRequest request) {
    return geneRepo.countAllByBioTypes(
        request.getRequestMetadata().getConnectionParams(), request.getBioTypesList());
  }

  @Override
  public Uni<CountResponse> countAllBySlice(CountAllBySliceRequest request) {
    return null;
  }

  @Override
  public Uni<CountResponse> countAllBySources(CountAllBySourcesRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllAltAlleles(FetchAllAltAllelesRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByBioTypes(FetchAllByBioTypesRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByDbIdList(FetchAllByDbIdListRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByDescription(FetchAllByDescriptionRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByDisplayLabel(FetchAllByDisplayLabelRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByExonSupportingEvidence(
      FetchAllByExonSupportingEvidenceRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByExternalName(FetchAllByExternalNameRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByOntologyLinkageType(
      FetchAllByOntologyLinkageTypeRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllBySlice(FetchAllBySliceRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllBySliceAndExternalDbNameLink(
      FetchAllBySliceAndExternalDbNameLinkRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllBySources(FetchAllBySourcesRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByStableIdList(FetchAllByStableIdListRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByTranscriptSupportingEvidence(
      FetchAllByTranscriptSupportingEvidenceRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllVersionsByStableId(
      FetchAllVersionsByStableIdRequest request) {
    return null;
  }

  @Override
  public Uni<Gene> fetchByDbId(FetchByDbIdRequest request) {
    return geneRepo.findByDbId(
        request.getRequestMetadata().getConnectionParams(), request.getDbId());
  }

  @Override
  public Uni<Gene> fetchByDisplayLabel(FetchByDisplayLabelRequest request) {
    return null;
  }

  @Override
  public Uni<Gene> fetchByExonStableId(FetchByExonStableIdRequest request) {
    return null;
  }

  @Override
  public Uni<Gene> fetchByStableId(FetchByStableIdRequest request) {
    return null;
  }

  @Override
  public Uni<Gene> fetchByStableIdVersion(FetchByStableIdVersionRequest request) {
    return null;
  }

  @Override
  public Uni<Gene> fetchByTranscriptId(FetchByTranscriptIdRequest request) {
    return null;
  }

  @Override
  public Uni<Gene> fetchByTranscriptStableId(FetchByTranscriptStableIdRequest request) {
    return null;
  }

  @Override
  public Uni<Gene> fetchByTranslationStableId(FetchByTranslationStableIdRequest request) {
    return null;
  }

  @Override
  public Uni<StoreResponse> store(StoreRequest request) {
    return null;
  }
}