package org.ebi.ensembl.repo.handler;

import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ConnectionHandler {
  private final Vertx vertx;
  private final Map<String, Pool> multiPool;

  public ConnectionHandler(Vertx vertx) {
    this.vertx = vertx;
    this.multiPool = new ConcurrentHashMap<>(10);
  }

  public Pool pool(ConnectionParams params) {
    String key = key(params);
    if (multiPool.containsKey(key)) {
      return multiPool.get(key);
    }

    MySQLConnectOptions mySQLConnectOptions =
        new MySQLConnectOptions()
            .setPort(params.getPort())
            .setHost(params.getHost())
            .setDatabase(params.getDbName())
            .setUser(params.getUserName())
            .setPassword(params.getPassword());

    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
    Pool pool = Pool.pool(vertx, mySQLConnectOptions, poolOptions);
    multiPool.put(key, pool);

    return pool;
  }

  private String key(ConnectionParams params) {
    // jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB_NAME}
    return String.format(
        "jdbc:mysql://%s:%d/%s", params.getHost(), params.getPort(), params.getDbName());
  }
}