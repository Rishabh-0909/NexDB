package io.github.rishabh0909.nexdb.index;

import io.github.rishabh0909.nexdb.operation.query.Queryable;

public interface UniqueQueryableIndex<K extends Comparable<K>, V>
        extends UniqueTreeIndexManager<K, V>, Queryable<K, V>
{}
