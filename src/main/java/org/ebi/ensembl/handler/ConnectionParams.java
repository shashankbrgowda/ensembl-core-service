package org.ebi.ensembl.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionParams {
  private String host;
  private Integer port;
  private String dbName;
  private String userName;
  private String password;
  private RequestMetadata requestMetadata;

  public static ConnectionParams mapConnectionParams(
      org.ebi.ensembl.grpc.common.ConnectionParams connectionParams) {
    return ConnectionParams.builder()
        .host(connectionParams.getHost())
        .port(connectionParams.getPort())
        .dbName(connectionParams.getDbName())
        .userName(connectionParams.getUserName())
        .password(connectionParams.getPassword())
        .requestMetadata(
            RequestMetadata.builder()
                .species(connectionParams.getRequestMetadata().getSpecies())
                .group(connectionParams.getRequestMetadata().getGroup())
                .type(connectionParams.getRequestMetadata().getType())
                .build())
        .build();
  }
}
