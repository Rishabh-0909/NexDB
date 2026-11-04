package io.github.rishabh0909.nexdb.index;

import io.github.rishabh0909.nexdb.ds.KeyValue;
import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.exception.DeserializationException;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.operation.query.Order;
import io.github.rishabh0909.nexdb.utils.LockableIterator;

import java.util.ListIterator;
import java.util.Optional;

public interface DuplicateIndexManager<K extends Comparable<K>, V extends Number & Comparable<V>> extends NullableIndex<V> {
    boolean addIndex(K identifier, V value) throws InternalOperationException, DeserializationException;
    Optional<ListIterator<V>> getIndex(K identifier) throws InternalOperationException;
    Optional<ListIterator<V>> getIndex(K identifier, Order order) throws InternalOperationException;
    boolean removeIndex(K identifier, V value) throws InternalOperationException, DeserializationException;
    int size() throws InternalOperationException;
    LockableIterator<KeyValue<K, ListIterator<V>>> getSortedIterator(Order order) throws InternalOperationException;
    void purgeIndex() throws InternalOperationException;
    int getIndexId();
    UniqueTreeIndexManager<K, Pointer> getInnerIndexManager();
}
