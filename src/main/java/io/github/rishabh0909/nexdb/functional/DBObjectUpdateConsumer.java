package io.github.rishabh0909.nexdb.functional;


import io.github.rishabh0909.nexdb.exception.InvalidDBObjectWrapper;

@FunctionalInterface
public interface DBObjectUpdateConsumer<T> {
    void accept(T t) throws InvalidDBObjectWrapper;
}
