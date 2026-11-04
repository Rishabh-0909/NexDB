package io.github.rishabh0909.nexdb.operation.query;

import io.github.rishabh0909.nexdb.operation.CollectionIndexProvider;

import java.util.Iterator;

public interface Condition {
    <V extends Number & Comparable<V>> Iterator<V> evaluate(CollectionIndexProvider collectionIndexProvider, Order order);
    String getField();
}
