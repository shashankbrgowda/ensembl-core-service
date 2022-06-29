package org.ebi.ensembl.domain.adaptor.impl;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.domain.adaptor.CoreAdaptor;
import org.ebi.ensembl.domain.model.Transcript;

public class TranscriptAdaptor implements CoreAdaptor<Transcript> {
  @Override
  public Uni<Transcript> findByDbId(Integer dbId) {
    return null;
  }
}
