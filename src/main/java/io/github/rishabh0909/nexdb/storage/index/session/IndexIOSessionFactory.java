package io.github.rishabh0909.nexdb.storage.index.session;

import io.github.rishabh0909.nexdb.ds.KVSize;
import io.github.rishabh0909.nexdb.index.tree.node.NodeFactory;
import io.github.rishabh0909.nexdb.storage.index.IndexStorageManager;

public abstract class IndexIOSessionFactory {
    public abstract <K extends Comparable<K>> IndexIOSession<K> create(IndexStorageManager indexStorageManager, int indexId, NodeFactory<K> nodeFactory, KVSize kvSize);
}