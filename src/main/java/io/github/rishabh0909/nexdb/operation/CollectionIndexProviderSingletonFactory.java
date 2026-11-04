package io.github.rishabh0909.nexdb.operation;

import io.github.rishabh0909.nexdb.scheme.Scheme;

import java.util.HashMap;
import java.util.Map;

public abstract class CollectionIndexProviderSingletonFactory {
    private final Map<Scheme.Collection, CollectionIndexProvider> providerMap = new HashMap<>();

    public final CollectionIndexProvider getInstance(Scheme.Collection collection) {
        return providerMap.computeIfAbsent(collection, this::create);
    }

    protected abstract CollectionIndexProvider create(Scheme.Collection collection);
}
