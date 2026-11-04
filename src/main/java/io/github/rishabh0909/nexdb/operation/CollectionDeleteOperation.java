package io.github.rishabh0909.nexdb.operation;

import io.github.rishabh0909.nexdb.exception.DeserializationException;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.operation.query.Query;

public interface CollectionDeleteOperation<T extends Number & Comparable<T>> {
    CollectionDeleteOperation<T> query(Query query);
    Query getQuery();
    long execute() throws InternalOperationException, DeserializationException;
}
