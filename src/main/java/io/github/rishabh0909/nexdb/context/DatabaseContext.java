package io.github.rishabh0909.nexdb.context;

import io.github.rishabh0909.nexdb.operation.CollectionOperation;
import io.github.rishabh0909.nexdb.scheme.Scheme;

public interface DatabaseContext {
    Scheme getScheme();
    CollectionOperation getOperation(Scheme.Collection collection);
    CollectionOperation getOperation(String collection);
    void shutdownGracefully();
}
