package io.github.rishabh0909.nexdb.index.tree.node.cluster;

import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.index.data.IndexBinaryObjectFactory;
import io.github.rishabh0909.nexdb.index.data.PointerIndexBinaryObject;
import io.github.rishabh0909.nexdb.index.tree.BPlusTreeUniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.index.tree.node.NodeFactory;
import io.github.rishabh0909.nexdb.storage.index.IndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.session.ImmediateCommitIndexIOSession;
import io.github.rishabh0909.nexdb.storage.index.session.IndexIOSessionFactory;

public class ClusterBPlusTreeUniqueTreeIndexManager<K extends Comparable<K>> extends BPlusTreeUniqueTreeIndexManager<K, Pointer> {
    public ClusterBPlusTreeUniqueTreeIndexManager(int index, int degree, IndexStorageManager indexStorageManager, IndexIOSessionFactory indexIOSessionFactory, IndexBinaryObjectFactory<K> keyIndexBinaryObjectFactory){
        super(index, degree, indexStorageManager, indexIOSessionFactory, keyIndexBinaryObjectFactory, new PointerIndexBinaryObject.Factory(), new NodeFactory.ClusterNodeFactory<>(keyIndexBinaryObjectFactory));
    }

    public ClusterBPlusTreeUniqueTreeIndexManager(int index, int degree, IndexStorageManager indexStorageManager, IndexBinaryObjectFactory<K> keyIndexBinaryObjectFactory){
        this(index, degree, indexStorageManager, ImmediateCommitIndexIOSession.Factory.getInstance(), keyIndexBinaryObjectFactory);
    }

}
