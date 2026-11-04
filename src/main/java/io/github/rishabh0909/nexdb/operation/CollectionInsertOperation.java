package io.github.rishabh0909.nexdb.operation;

import io.github.rishabh0909.nexdb.exception.DeserializationException;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.exception.SerializationException;

public interface CollectionInsertOperation<T extends Number & Comparable<T>> {
    <V> V execute(V v) throws SerializationException, InternalOperationException, DeserializationException;
    byte[] execute(byte[] bytes) throws InternalOperationException, DeserializationException, SerializationException;
}
