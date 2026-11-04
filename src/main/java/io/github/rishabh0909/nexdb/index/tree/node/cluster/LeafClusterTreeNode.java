package io.github.rishabh0909.nexdb.index.tree.node.cluster;

import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.index.data.IndexBinaryObjectFactory;
import io.github.rishabh0909.nexdb.index.data.PointerIndexBinaryObject;
import io.github.rishabh0909.nexdb.index.tree.node.AbstractLeafTreeNode;

public class LeafClusterTreeNode<K extends Comparable<K>> extends AbstractLeafTreeNode<K, Pointer> {
    public LeafClusterTreeNode(byte[] data, IndexBinaryObjectFactory<K> keyIndexBinaryObjectFactory) {
        super(data, keyIndexBinaryObjectFactory, new PointerIndexBinaryObject.Factory());
    }
}