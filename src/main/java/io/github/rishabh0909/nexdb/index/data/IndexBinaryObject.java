package io.github.rishabh0909.nexdb.index.data;

import io.github.rishabh0909.nexdb.exception.DeserializationException;

public interface IndexBinaryObject<E> {
    E asObject() throws DeserializationException;
    int size();
    byte[] getBytes();
    default E getFirst() {
        throw new UnsupportedOperationException();
    }
    default E getNext(E current) {
        throw new UnsupportedOperationException();
    }
    default E getNext() throws DeserializationException {
        throw new UnsupportedOperationException();
    }
    default boolean supportIncrements() {
        return false;
    }
}
