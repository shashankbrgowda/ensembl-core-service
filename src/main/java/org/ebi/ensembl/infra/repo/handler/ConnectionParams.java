package org.ebi.ensembl.infra.repo.handler;

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
}
