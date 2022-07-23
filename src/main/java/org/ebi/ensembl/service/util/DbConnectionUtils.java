package org.ebi.ensembl.service.util;

import org.ebi.ensembl.grpc.common.ConnectionParams;

public final class DbConnectionUtils {
  private DbConnectionUtils() {}

  public static String key(ConnectionParams params) {
    // jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB_NAME}
    return String.format(
        "jdbc:mysql://%s:%d/%s", params.getHost(), params.getPort(), params.getDbName());
  }
}
