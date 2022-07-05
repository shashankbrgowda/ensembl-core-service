package org.ebi.ensembl.domain.service.impl;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.application.model.Transcript;
import org.ebi.ensembl.domain.service.CoreService;
import org.ebi.ensembl.infra.repo.handler.ConnectionParams;

public class TranscriptService implements CoreService<Transcript> {
  @Override
  public Uni<Transcript> fetchByDbId(ConnectionParams params, Integer dbId) {
    return null;
  }
}
