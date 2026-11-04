package io.github.rishabh0909.nexdb.index.data;

import io.github.rishabh0909.nexdb.exception.IndexBinaryObjectCreationException;

public interface IndexBinaryObjectFactory<E> {
    IndexBinaryObject<E> create(E e) throws IndexBinaryObjectCreationException;
    IndexBinaryObject<E> create(byte[] bytes, int beginning);
    default IndexBinaryObject<E> create(byte[] bytes) {
        return create(bytes, 0);
    }
    int size();
    Class<E> getType();
    IndexBinaryObject<E> createEmpty();
}
