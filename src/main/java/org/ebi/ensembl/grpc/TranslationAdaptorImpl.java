package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.common.Translation;
import org.ebi.ensembl.grpc.translation.FetchTranslationByDbIdRequest;
import org.ebi.ensembl.grpc.translation.FetchTranslationByStableIdRequest;
import org.ebi.ensembl.grpc.translation.FetchTranslationByTranscriptIdRequest;
import org.ebi.ensembl.grpc.translation.TranslationAdaptor;
import org.ebi.ensembl.repo.TranslationRepo;

@GrpcService
public class TranslationAdaptorImpl implements TranslationAdaptor {
  private final TranslationRepo translationRepo;

  public TranslationAdaptorImpl(TranslationRepo translationRepo) {
    this.translationRepo = translationRepo;
  }

  @Override
  public Uni<Translation> fetchByDbId(FetchTranslationByDbIdRequest request) {
    return translationRepo.fetchByDbId(
        request.getRequestMetadata().getConnectionParams(), request.getDbId());
  }

  @Override
  public Uni<Translation> fetchByStableId(FetchTranslationByStableIdRequest request) {
    return translationRepo.fetchByStableId(
        request.getRequestMetadata().getConnectionParams(), request.getStableId());
  }

  @Override
  public Uni<Translation> fetchByTranscriptId(FetchTranslationByTranscriptIdRequest request) {
    return translationRepo.fetchByTranscriptId(
        request.getRequestMetadata().getConnectionParams(), request.getTranscriptId());
  }
}
