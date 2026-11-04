package io.github.rishabh0909.nexdb.storage.index.session;

import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.index.tree.node.AbstractTreeNode;
import io.github.rishabh0909.nexdb.storage.index.IndexStorageManager;

import java.util.Optional;

public interface IndexIOSession<K extends Comparable<K>> {
    Optional<AbstractTreeNode<K>> getRoot() throws InternalOperationException;
    IndexStorageManager.NodeData write(AbstractTreeNode<K> node) throws InternalOperationException;
    AbstractTreeNode<K> read(Pointer pointer) throws InternalOperationException;
    void update(AbstractTreeNode<K> node) throws InternalOperationException;
    void remove(AbstractTreeNode<K> node) throws InternalOperationException;
    IndexStorageManager getIndexStorageManager();
    void commit() throws InternalOperationException;
}
