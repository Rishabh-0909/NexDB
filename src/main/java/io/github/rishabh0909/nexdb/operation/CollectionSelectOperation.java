package io.github.rishabh0909.nexdb.operation;

import io.github.rishabh0909.nexdb.operation.query.Query;
import io.github.rishabh0909.nexdb.storage.db.DBObject;
import io.github.rishabh0909.nexdb.utils.LockableIterator;

import java.util.List;

public interface CollectionSelectOperation<T extends Number & Comparable<T>> {
    CollectionSelectOperation<T> fields(String... fields);
    CollectionSelectOperation<T> query(Query query);
    Query getQuery();
    LockableIterator<DBObject> execute();
    <V> LockableIterator<V> execute(Class<V> clazz);
    <V> List<V> asList(Class<V> clazz);
    long count();
    boolean exists();
}
