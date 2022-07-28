package org.ebi.ensembl.util;

import org.ebi.ensembl.grpc.common.ConnectionParams;

import java.time.LocalDateTime;
import java.util.Objects;

public final class DbUtil {
  private DbUtil() {}

  public static final Class<Integer> iCls = Integer.class;
  public static final Class<String> sCls = String.class;
  public static final Class<Boolean> bCls = Boolean.class;
  public static final Class<LocalDateTime> ldtCls = LocalDateTime.class;

  public static String key(ConnectionParams params) {
    // jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB_NAME}
    return String.format(
        "jdbc:mysql://%s:%d/%s", params.getHost(), params.getPort(), params.getDbName());
  }

  public static <T> T protoDefaultValue(T t, Class<T> tClass) {
    if (Objects.nonNull(t)) {
      return t;
    }

    if(tClass.isAssignableFrom(String.class) || tClass.isAssignableFrom(LocalDateTime.class)) {
      return (T) "";
    } else if(tClass.isAssignableFrom(Integer.class)) {
      return (T) Integer.valueOf(0);
    } else if(tClass.isAssignableFrom(Boolean.class)) {
      return (T) Boolean.valueOf(false);
    } else {
      throw new IllegalArgumentException("Invalid row column type..");
    }
  }
}
