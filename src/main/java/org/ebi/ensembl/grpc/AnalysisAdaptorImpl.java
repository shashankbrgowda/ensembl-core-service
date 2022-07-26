package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.analysis.AnalysisAdaptor;
import org.ebi.ensembl.grpc.analysis.FetchAnalysisByDbIdRequest;
import org.ebi.ensembl.grpc.common.Analysis;
import org.ebi.ensembl.repo.AnalysisRepo;

@GrpcService
public class AnalysisAdaptorImpl implements AnalysisAdaptor {
  private final AnalysisRepo analysisRepo;

  public AnalysisAdaptorImpl(AnalysisRepo analysisRepo) {
    this.analysisRepo = analysisRepo;
  }

  @Override
  public Uni<Analysis> fetchByDbId(FetchAnalysisByDbIdRequest request) {
    return analysisRepo.fetchByDbId(
        request.getRequestMetadata().getConnectionParams(), request.getAnalysisId());
  }
}
