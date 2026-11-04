package io.github.rishabh0909.nexdb.index;

import io.github.rishabh0909.nexdb.operation.query.Queryable;

public interface DuplicateQueryableIndex<K extends Comparable<K>, V extends Number & Comparable<V>>
        extends DuplicateIndexManager<K, V>, Queryable<K, V>
{}
