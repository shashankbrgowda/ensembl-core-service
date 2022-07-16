package org.ebi.ensembl.repo;

import com.google.protobuf.ProtocolStringList;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.gene.CountResponse;
import org.ebi.ensembl.repo.handler.ConnectionParams;

public interface CoreRepo<T> {
  Uni<T> findByDbId(ConnectionParams params, Integer dbId);

  Uni<CountResponse> countAllByBioTypes(ConnectionParams params, ProtocolStringList bioTypes);
}
