package org.ebi.ensembl.util;

import org.ebi.ensembl.grpc.common.ConnectionParams;

public final class DbConnectionUtil {
  private DbConnectionUtil() {}

  public static String key(ConnectionParams params) {
    // jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB_NAME}
    return String.format(
        "jdbc:mysql://%s:%d/%s", params.getHost(), params.getPort(), params.getDbName());
  }
}
