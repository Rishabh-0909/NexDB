package io.github.rishabh0909.nexdb.index;

import io.github.rishabh0909.nexdb.ds.KeyValue;
import io.github.rishabh0909.nexdb.exception.DeserializationException;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.index.tree.node.AbstractTreeNode;
import io.github.rishabh0909.nexdb.operation.query.Order;
import io.github.rishabh0909.nexdb.utils.LockableIterator;

import java.util.Optional;

public interface UniqueTreeIndexManager<K extends Comparable<K>, V> extends NullableIndex<V> {
    AbstractTreeNode<K> addIndex(K identifier, V value) throws InternalOperationException;
    AbstractTreeNode<K> addOrUpdateIndex(K identifier, V value) throws InternalOperationException;
    Optional<V> getIndex(K identifier) throws InternalOperationException;
    boolean removeIndex(K identifier) throws InternalOperationException;
    int size() throws InternalOperationException;
    LockableIterator<KeyValue<K, V>> getSortedIterator(Order order) throws InternalOperationException;
    void purgeIndex() throws InternalOperationException;
    int getIndexId();
    boolean supportIncrement();
    K nextKey() throws InternalOperationException, DeserializationException;
}
