package io.github.rishabh0909.nexdb.operation;

import io.github.rishabh0909.nexdb.exception.BaseSerializationException;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.operation.query.Query;

import java.util.function.Consumer;

public interface CollectionUpdateOperation<T extends Number & Comparable<T>> {
    CollectionUpdateOperation<T> query(Query query);
    Query getQuery();
    <M> long execute(Class<M> mClass, Consumer<M> mConsumer) throws InternalOperationException, BaseSerializationException;
    long execute(Consumer<byte[]> byteArrayConsumer) throws InternalOperationException, BaseSerializationException;
}
