package org.ebi.ensembl.domain.adaptor.impl;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.application.model.Transcript;
import org.ebi.ensembl.domain.adaptor.CoreAdaptor;
import org.ebi.ensembl.infra.repo.handler.ConnectionParams;

public class TranscriptAdaptor implements CoreAdaptor<Transcript> {
  @Override
  public Uni<Transcript> fetchByDbId(ConnectionParams params, Integer dbId) {
    return null;
  }
}
