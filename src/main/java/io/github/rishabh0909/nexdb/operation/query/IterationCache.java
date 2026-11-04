package io.github.rishabh0909.nexdb.operation.query;

public interface IterationCache<T extends Number> {
    void add(int cacheIndex, T value);

    boolean cacheInitialized(int cacheIndex);

    boolean contains(int cacheIndex, T value);
}
