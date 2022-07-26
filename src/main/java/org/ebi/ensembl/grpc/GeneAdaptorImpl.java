package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.common.*;
import org.ebi.ensembl.grpc.gene.*;
import org.ebi.ensembl.repo.AnalysisRepo;
import org.ebi.ensembl.repo.GeneRepo;
import org.ebi.ensembl.repo.SequenceRegionRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Error handling
@GrpcService
public class GeneAdaptorImpl implements GeneAdaptor {
  private final GeneRepo geneRepo;
  private final SequenceRegionRepo sequenceRegionRepo;
  private final AnalysisRepo analysisRepo;

  public GeneAdaptorImpl(GeneRepo geneRepo, SequenceRegionRepo sequenceRegionRepo, AnalysisRepo analysisRepo) {
    this.geneRepo = geneRepo;
    this.sequenceRegionRepo = sequenceRegionRepo;
    this.analysisRepo = analysisRepo;
  }

  @Override
  public Uni<CountResponse> countAllByBioTypes(CountAllGenesByBioTypesRequest request) {
    return geneRepo.countAllByBioTypes(
        request.getRequestMetadata().getConnectionParams(), request.getBioTypesList());
  }

  @Override
  public Uni<MultiGeneResponse> fetchAllByDbIdList(FetchAllGenesByDbIdListRequest request) {
    List<Uni<Gene>> geneUniList =
        request.getDbIdsList().stream()
            .map(
                id -> {
                  FetchGeneByDbIdRequest fetchGeneByDbIdRequest =
                      FetchGeneByDbIdRequest.newBuilder()
                          .setDbId(id)
                          .setRequestMetadata(request.getRequestMetadata())
                          .build();
                  return fetchByDbId(fetchGeneByDbIdRequest);
                })
            .collect(Collectors.toList());

    return Uni.combine()
        .all()
        .unis(geneUniList)
        .combinedWith(
            l -> {
              List<Gene> geneList = new ArrayList<>();
              l.forEach(g -> geneList.add((Gene) g));
              return MultiGeneResponse.newBuilder().addAllGenes(geneList).build();
            });
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
    ConnectionParams connectionParams = request.getRequestMetadata().getConnectionParams();
    return geneRepo
        .findByDbId(connectionParams, request.getDbId())
        .onItem()
        .transformToUni(
            gene -> {
              Gene.Builder builder = gene.toBuilder();
              return sequenceRegionRepo
                  .fetchBySeqRegionId(connectionParams, gene.getSeqRegionId())
                  .onItem()
                  .transform(slice -> builder.setSlice(slice).build());
            })
        .onItem()
        .transformToUni(
            gene -> {
              Gene.Builder builder = gene.toBuilder();
              return analysisRepo
                  .fetchByDbId(connectionParams, gene.getAnalysisId())
                  .onItem()
                  .transform(analysis -> builder.setAnalysis(analysis).build());
            });
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
