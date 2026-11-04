package io.github.rishabh0909.nexdb.index;

import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.operation.query.Order;

import java.util.Iterator;

public interface NullableIndex<V> {
    default boolean isNull(V value) {
        throw new UnsupportedOperationException();
    }

    default void addNull(V value) throws InternalOperationException {
        throw new UnsupportedOperationException();
    }

    default void removeNull(V value) throws InternalOperationException {
        throw new UnsupportedOperationException();
    }

    default Iterator<V> getNullIndexes(Order order) {
        throw new UnsupportedOperationException();
    }

}
