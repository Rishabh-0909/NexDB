package io.github.rishabh0909.nexdb.storage.index;

import io.github.rishabh0909.nexdb.context.EngineConfig;
import io.github.rishabh0909.nexdb.scheme.Scheme;
import io.github.rishabh0909.nexdb.storage.index.header.IndexHeaderManagerSingletonFactory;

public abstract class IndexStorageManagerSingletonFactory {
    protected final EngineConfig engineConfig;
    protected final IndexHeaderManagerSingletonFactory indexHeaderManagerSingletonFactory;

    protected IndexStorageManagerSingletonFactory(EngineConfig engineConfig, IndexHeaderManagerSingletonFactory indexHeaderManagerSingletonFactory) {
        this.engineConfig = engineConfig;
        this.indexHeaderManagerSingletonFactory = indexHeaderManagerSingletonFactory;
    }

    public abstract IndexStorageManager create(Scheme scheme, Scheme.Collection collection);
}
