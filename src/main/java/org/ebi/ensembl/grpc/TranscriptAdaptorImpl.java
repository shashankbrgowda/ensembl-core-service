package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.grpc.common.Transcript;
import org.ebi.ensembl.grpc.transcript.*;
import org.ebi.ensembl.repo.AnalysisRepo;
import org.ebi.ensembl.repo.SequenceRegionRepo;
import org.ebi.ensembl.repo.TranscriptRepo;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class TranscriptAdaptorImpl implements TranscriptAdaptor {
  private final TranscriptRepo transcriptRepo;
  private final SequenceRegionRepo sequenceRegionRepo;
  private final AnalysisRepo analysisRepo;

  public TranscriptAdaptorImpl(
      TranscriptRepo transcriptRepo,
      SequenceRegionRepo sequenceRegionRepo,
      AnalysisRepo analysisRepo) {
    this.transcriptRepo = transcriptRepo;
    this.sequenceRegionRepo = sequenceRegionRepo;
    this.analysisRepo = analysisRepo;
  }

  @Override
  public Uni<MultiTranscriptResponse> fetchAllBySlice(FetchAllTranscriptsBySliceRequest request) {
    ConnectionParams connectionParams = request.getRequestMetadata().getConnectionParams();
    return transcriptRepo
        .fetchAllBySlice(connectionParams, request.getSlice())
        .onItem()
        .transformToUni(
            transcript -> {
              Transcript.Builder builder = transcript.toBuilder();
              return sequenceRegionRepo
                  .fetchBySeqRegionId(connectionParams, transcript.getSeqRegionId())
                  .onItem()
                  .transform(slice -> builder.setSlice(slice).build());
            })
        .merge()
        .onItem()
        .transformToUni(
            transcript -> {
              Transcript.Builder builder = transcript.toBuilder();
              return analysisRepo
                  .fetchByDbId(connectionParams, transcript.getAnalysisId())
                  .onItem()
                  .transform(analysis -> builder.setAnalysis(analysis).build());
            })
        .merge()
        .collect()
        .asList()
        .onItem()
        .transformToUni(
            transcripts ->
                Uni.createFrom()
                    .item(
                        MultiTranscriptResponse.newBuilder()
                            .addAllTranscripts(transcripts)
                            .build()));
  }

  @Override
  public Uni<MultiTranscriptResponse> fetchAllByDbIdList(
      FetchAllTranscriptsByDbIdListRequest request) {
    List<Uni<Transcript>> transcriptUniList =
        request.getDbIdsList().stream()
            .map(
                id -> {
                  FetchTranscriptByDbIdRequest fetchTranscriptByDbIdRequest =
                      FetchTranscriptByDbIdRequest.newBuilder()
                          .setDbId(id)
                          .setRequestMetadata(request.getRequestMetadata())
                          .build();
                  return fetchByDbId(fetchTranscriptByDbIdRequest);
                })
            .toList();

    return Uni.combine()
        .all()
        .unis(transcriptUniList)
        .combinedWith(
            l -> {
              List<Transcript> transcriptList = new ArrayList<>();
              l.forEach(t -> transcriptList.add((Transcript) t));
              return MultiTranscriptResponse.newBuilder().addAllTranscripts(transcriptList).build();
            });
  }

  @Override
  public Uni<MultiTranscriptResponse> fetchAllByStableIdList(
      FetchAllTranscriptsByStableIdListRequest request) {
    List<Uni<Transcript>> transcriptUniList =
        request.getStableIdsList().stream()
            .map(
                id -> {
                  FetchTranscriptByStableIdRequest fetchTranscriptByStableIdRequest =
                      FetchTranscriptByStableIdRequest.newBuilder()
                          .setStableId(id)
                          .setRequestMetadata(request.getRequestMetadata())
                          .build();
                  return fetchByStableId(fetchTranscriptByStableIdRequest);
                })
            .toList();

    return Uni.combine()
        .all()
        .unis(transcriptUniList)
        .combinedWith(
            l -> {
              List<Transcript> transcriptList = new ArrayList<>();
              l.forEach(t -> transcriptList.add((Transcript) t));
              return MultiTranscriptResponse.newBuilder().addAllTranscripts(transcriptList).build();
            });
  }

  @Override
  public Uni<Transcript> fetchByDbId(FetchTranscriptByDbIdRequest request) {
    ConnectionParams connectionParams = request.getRequestMetadata().getConnectionParams();
    return transcriptRepo
        .findByDbId(connectionParams, request.getDbId())
        .onItem()
        .transformToUni(
            transcript -> {
              Transcript.Builder builder = transcript.toBuilder();
              return sequenceRegionRepo
                  .fetchBySeqRegionId(connectionParams, transcript.getSeqRegionId())
                  .onItem()
                  .transform(slice -> builder.setSlice(slice).build());
            })
        .onItem()
        .transformToUni(
            transcript -> {
              Transcript.Builder builder = transcript.toBuilder();
              return analysisRepo
                  .fetchByDbId(connectionParams, transcript.getAnalysisId())
                  .onItem()
                  .transform(analysis -> builder.setAnalysis(analysis).build());
            });
  }

  @Override
  public Uni<Transcript> fetchByStableId(FetchTranscriptByStableIdRequest request) {
    ConnectionParams connectionParams = request.getRequestMetadata().getConnectionParams();
    return transcriptRepo
        .findByStableId(connectionParams, request.getStableId())
        .onItem()
        .transformToUni(
            transcript -> {
              Transcript.Builder builder = transcript.toBuilder();
              return sequenceRegionRepo
                  .fetchBySeqRegionId(connectionParams, transcript.getSeqRegionId())
                  .onItem()
                  .transform(slice -> builder.setSlice(slice).build());
            })
        .onItem()
        .transformToUni(
            transcript -> {
              Transcript.Builder builder = transcript.toBuilder();
              return analysisRepo
                  .fetchByDbId(connectionParams, transcript.getAnalysisId())
                  .onItem()
                  .transform(analysis -> builder.setAnalysis(analysis).build());
            });
  }

  @Override
  public Uni<MultiTranscriptResponse> fetchAllByGeneId(FetchAllTranscriptsByGeneIdRequest request) {
    ConnectionParams connectionParams = request.getRequestMetadata().getConnectionParams();
    return transcriptRepo
        .fetchAllByGeneId(connectionParams, request.getGeneId())
        .onItem()
        .transformToUni(
            transcript -> {
              Transcript.Builder builder = transcript.toBuilder();
              return sequenceRegionRepo
                  .fetchBySeqRegionId(connectionParams, transcript.getSeqRegionId())
                  .onItem()
                  .transform(slice -> builder.setSlice(slice).build());
            })
        .merge()
        .onItem()
        .transformToUni(
            transcript -> {
              Transcript.Builder builder = transcript.toBuilder();
              return analysisRepo
                  .fetchByDbId(connectionParams, transcript.getAnalysisId())
                  .onItem()
                  .transform(analysis -> builder.setAnalysis(analysis).build());
            })
        .merge()
        .collect()
        .asList()
        .onItem()
        .transformToUni(
            transcripts ->
                Uni.createFrom()
                    .item(
                        MultiTranscriptResponse.newBuilder()
                            .addAllTranscripts(transcripts)
                            .build()));
  }
}
