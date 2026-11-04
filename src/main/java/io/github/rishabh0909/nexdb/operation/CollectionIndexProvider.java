package io.github.rishabh0909.nexdb.operation;

import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.index.DuplicateQueryableIndex;
import io.github.rishabh0909.nexdb.index.UniqueQueryableIndex;
import io.github.rishabh0909.nexdb.index.UniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.operation.query.Queryable;
import io.github.rishabh0909.nexdb.scheme.Scheme;


public interface CollectionIndexProvider {
    UniqueQueryableIndex<?, ? extends Number> getUniqueIndexManager(Scheme.Field field);
    DuplicateQueryableIndex<?, ? extends Number> getDuplicateIndexManager(Scheme.Field field);
    UniqueTreeIndexManager<?, Pointer> getClusterIndexManager();
    UniqueQueryableIndex<?, ? extends Number> getUniqueIndexManager(String fieldName);
    DuplicateQueryableIndex<?, ? extends Number> getDuplicateIndexManager(String fieldName);
    Queryable<?, ? extends Number> getQueryableIndex(Scheme.Field field);
    Queryable<?, ? extends Number> getQueryableIndex(String fieldName);
}
