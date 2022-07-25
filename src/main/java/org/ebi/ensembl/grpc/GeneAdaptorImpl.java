package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.common.CountResponse;
import org.ebi.ensembl.grpc.common.Gene;
import org.ebi.ensembl.grpc.gene.*;
import org.ebi.ensembl.repo.GeneRepo;

// TODO: Error handling
@GrpcService
public class GeneAdaptorImpl implements GeneAdaptor {
  private final GeneRepo geneRepo;

  public GeneAdaptorImpl(GeneRepo geneRepo) {
    this.geneRepo = geneRepo;
  }

  @Override
  public Uni<CountResponse> countAllByBioTypes(CountAllGenesByBioTypesRequest request) {
    return geneRepo.countAllByBioTypes(
        request.getRequestMetadata().getConnectionParams(), request.getBioTypesList());
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByDbIdList(FetchAllGenesByDbIdListRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllBySlice(FetchAllGenesBySliceRequest request) {
    return null;
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByStableIdList(FetchAllGenesByStableIdListRequest request) {
    return null;
  }

  @Override
  public Uni<Gene> fetchByDbId(FetchGeneByDbIdRequest request) {
    return geneRepo.findByDbId(
        request.getRequestMetadata().getConnectionParams(), request.getDbId());
  }

  @Override
  public Uni<Gene> fetchByStableId(FetchGeneByStableIdRequest request) {
    return null;
  }

  @Override
  public Uni<Gene> fetchByTranscriptId(FetchGeneByTranscriptIdRequest request) {
    return null;
  }

  @Override
  public Uni<Gene> fetchByTranscriptStableId(FetchGeneByTranscriptStableIdRequest request) {
    return null;
  }

  @Override
  public Uni<Gene> fetchByTranslationStableId(FetchGeneByTranslationStableIdRequest request) {
    return null;
  }

}
