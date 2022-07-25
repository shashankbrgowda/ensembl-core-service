package org.ebi.ensembl.util;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.function.Predicate;

public final class MutinyUtil {
  private MutinyUtil() {}

  public static <T> Multi<T> combineMultiAndMerge(Multi<T> multi1, Multi<T> multi2) {
    return Multi.createBy()
        .combining()
        .streams(multi1, multi2)
        .asTuple()
        .onItem()
        .transformToMultiAndMerge(
            tuple2 -> Multi.createFrom().items(tuple2.getItem1(), tuple2.getItem2()));
  }

  public static <T> Uni<List<T>> filterAndCollectMultiAsList(
      Multi<T> multi, Predicate<? super T> predicate) {
    return multi.select().where(predicate).collect().asList();
  }
}
