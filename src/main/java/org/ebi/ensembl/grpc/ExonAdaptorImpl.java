package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.grpc.common.Exon;
import org.ebi.ensembl.grpc.common.Transcript;
import org.ebi.ensembl.grpc.exon.*;
import org.ebi.ensembl.grpc.transcript.MultiTranscriptResponse;
import org.ebi.ensembl.repo.ExonRepo;
import org.ebi.ensembl.repo.SequenceRegionRepo;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class ExonAdaptorImpl implements ExonAdaptor {
  private final ExonRepo exonRepo;
  private final SequenceRegionRepo sequenceRegionRepo;

  public ExonAdaptorImpl(ExonRepo exonRepo, SequenceRegionRepo sequenceRegionRepo) {
    this.exonRepo = exonRepo;
    this.sequenceRegionRepo = sequenceRegionRepo;
  }

  @Override
  public Uni<MultiExonResponse> fetchAllBySlice(FetchAllExonsBySliceRequest request) {
    ConnectionParams connectionParams = request.getRequestMetadata().getConnectionParams();
    return exonRepo
        .fetchAllBySlice(connectionParams, request.getSlice())
        .onItem()
        .transformToUni(
            exon -> {
              Exon.Builder builder = exon.toBuilder();
              return sequenceRegionRepo
                  .fetchBySeqRegionId(connectionParams, exon.getSeqRegionId())
                  .onItem()
                  .transform(slice -> builder.setSlice(slice).build());
            })
        .merge()
        .collect()
        .asList()
        .onItem()
        .transformToUni(
            exons ->
                Uni.createFrom().item(MultiExonResponse.newBuilder().addAllExons(exons).build()));
  }

  @Override
  public Uni<MultiExonResponse> fetchAllByDbIdList(FetchAllExonsByDbIdListRequest request) {
    List<Uni<Exon>> exonUniList =
        request.getDbIdsList().stream()
            .map(
                id -> {
                  FetchExonByDbIdRequest fetchExonByDbIdRequest =
                      FetchExonByDbIdRequest.newBuilder()
                          .setDbId(id)
                          .setRequestMetadata(request.getRequestMetadata())
                          .build();
                  return fetchByDbId(fetchExonByDbIdRequest);
                })
            .toList();

    return Uni.combine()
        .all()
        .unis(exonUniList)
        .combinedWith(
            l -> {
              List<Exon> exonList = new ArrayList<>();
              l.forEach(e -> exonList.add((Exon) e));
              return MultiExonResponse.newBuilder().addAllExons(exonList).build();
            });
  }

  @Override
  public Uni<MultiExonResponse> fetchAllByStableIdList(FetchAllExonsByStableIdListRequest request) {
    List<Uni<Exon>> exonUniList =
        request.getStableIdsList().stream()
            .map(
                id -> {
                  FetchExonByStableIdRequest fetchExonByStableIdRequest =
                      FetchExonByStableIdRequest.newBuilder()
                          .setStableId(id)
                          .setRequestMetadata(request.getRequestMetadata())
                          .build();
                  return fetchByStableId(fetchExonByStableIdRequest);
                })
            .toList();

    return Uni.combine()
        .all()
        .unis(exonUniList)
        .combinedWith(
            l -> {
              List<Exon> exonList = new ArrayList<>();
              l.forEach(e -> exonList.add((Exon) e));
              return MultiExonResponse.newBuilder().addAllExons(exonList).build();
            });
  }

  @Override
  public Uni<Exon> fetchByDbId(FetchExonByDbIdRequest request) {
    ConnectionParams connectionParams = request.getRequestMetadata().getConnectionParams();
    return exonRepo
        .findByDbId(connectionParams, request.getDbId())
        .onItem()
        .transformToUni(
            exon -> {
              Exon.Builder builder = exon.toBuilder();
              return sequenceRegionRepo
                  .fetchBySeqRegionId(connectionParams, exon.getSeqRegionId())
                  .onItem()
                  .transform(slice -> builder.setSlice(slice).build());
            });
  }

  @Override
  public Uni<Exon> fetchByStableId(FetchExonByStableIdRequest request) {
    ConnectionParams connectionParams = request.getRequestMetadata().getConnectionParams();
    return exonRepo
        .findByStableId(connectionParams, request.getStableId())
        .onItem()
        .transformToUni(
            exon -> {
              Exon.Builder builder = exon.toBuilder();
              return sequenceRegionRepo
                  .fetchBySeqRegionId(connectionParams, exon.getSeqRegionId())
                  .onItem()
                  .transform(slice -> builder.setSlice(slice).build());
            });
  }

  @Override
  public Uni<MultiExonResponse> fetchAllByTranscriptId(FetchAllExonsByTranscriptIdRequest request) {
    ConnectionParams connectionParams = request.getRequestMetadata().getConnectionParams();
    return exonRepo
        .fetchAllByTranscriptId(connectionParams, request.getTranscriptId())
        .onItem()
        .transformToUni(
            exon -> {
              Exon.Builder builder = exon.toBuilder();
              return sequenceRegionRepo
                  .fetchBySeqRegionId(connectionParams, exon.getSeqRegionId())
                  .onItem()
                  .transform(slice -> builder.setSlice(slice).build());
            })
        .merge()
        .collect()
        .asList()
        .onItem()
        .transformToUni(
            exons ->
                Uni.createFrom().item(MultiExonResponse.newBuilder().addAllExons(exons).build()));
  }
}
