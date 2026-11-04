package io.github.rishabh0909.nexdb.index.data;

import lombok.Getter;

@Getter
public abstract class AbstractIndexBinaryObject<E extends Comparable<E>> implements IndexBinaryObject<E> {
    protected byte[] bytes;

    public AbstractIndexBinaryObject() {
    }

    protected AbstractIndexBinaryObject(byte[] bytes) {
        this.bytes = bytes;
    }
}
