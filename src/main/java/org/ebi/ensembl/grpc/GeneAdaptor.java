package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.repo.CoreRepo;
import org.ebi.ensembl.repo.handler.ConnectionParams;

@GrpcService
public class GeneAdaptor implements GeneSvc {
  private final CoreRepo<Gene> geneCoreRepo;

  public GeneAdaptor(CoreRepo<Gene> geneCoreRepo) {
    this.geneCoreRepo = geneCoreRepo;
  }

  @Override
  public Uni<CountResponse> countAllByBioTypes(CountAllByBioTypesRequest request) {
    return geneCoreRepo.countAllByBioTypes(
        mapConnectionParams(request.getParams()), request.getBioTypesList());
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
    return geneCoreRepo.findByDbId(mapConnectionParams(request.getParams()), request.getDbId());
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
}
