package io.github.rishabh0909.nexdb.operation;

public interface CollectionOperationsProvider {
    <T extends Number & Comparable<T>> CollectionSelectOperation<T> newSelectOperation();
    <T extends Number & Comparable<T>> CollectionInsertOperation<T> newInsertOperation();
}
