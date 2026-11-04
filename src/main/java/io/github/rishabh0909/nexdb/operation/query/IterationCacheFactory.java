package io.github.rishabh0909.nexdb.operation.query;

public interface IterationCacheFactory {
    <T extends Number> IterationCache<T> create();
}
