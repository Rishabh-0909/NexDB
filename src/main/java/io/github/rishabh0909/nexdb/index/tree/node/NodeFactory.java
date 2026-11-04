package io.github.rishabh0909.nexdb.index.tree.node;

import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.index.data.IndexBinaryObjectFactory;
import io.github.rishabh0909.nexdb.index.tree.node.cluster.LeafClusterTreeNode;
import io.github.rishabh0909.nexdb.storage.index.IndexStorageManager;

import static io.github.rishabh0909.nexdb.index.tree.node.AbstractTreeNode.TYPE_LEAF_NODE_BIT;

public interface NodeFactory<K extends Comparable<K>> {
    AbstractTreeNode<K> fromBytes(byte[] bytes);
    default AbstractTreeNode<K> fromBytes(byte[] bytes, Pointer pointer) {
        AbstractTreeNode<K> treeNode = this.fromBytes(bytes);
        treeNode.setPointer(pointer);
        return treeNode;
    }

    default AbstractTreeNode<K> fromNodeData(IndexStorageManager.NodeData nodeData){
        return this.fromBytes(nodeData.bytes(), nodeData.pointer());
    }
    AbstractTreeNode<K> fromBytes(byte[] emptyNode, AbstractTreeNode.Type type);


    class DefaultNodeFactory<K extends Comparable<K>> implements NodeFactory<K> {
        private final IndexBinaryObjectFactory<K> keyIndexBinaryObjectFactory;
        private final IndexBinaryObjectFactory<?> valueIndexBinaryObjectFactory;

        public DefaultNodeFactory(IndexBinaryObjectFactory<K> keyIndexBinaryObjectFactory, IndexBinaryObjectFactory<?> valueIndexBinaryObjectFactory) {
            this.keyIndexBinaryObjectFactory = keyIndexBinaryObjectFactory;
            this.valueIndexBinaryObjectFactory = valueIndexBinaryObjectFactory;
        }

        @Override
        public AbstractTreeNode<K> fromBytes(byte[] bytes) {
            if ((bytes[0] & TYPE_LEAF_NODE_BIT) == TYPE_LEAF_NODE_BIT)
                return new AbstractLeafTreeNode<>(bytes, keyIndexBinaryObjectFactory, valueIndexBinaryObjectFactory);
            return new InternalTreeNode<>(bytes, keyIndexBinaryObjectFactory);
        }

        @Override
        public AbstractTreeNode<K> fromBytes(byte[] bytes, AbstractTreeNode.Type type) {
            if (type.equals(AbstractTreeNode.Type.LEAF))
                return new AbstractLeafTreeNode<>(bytes, keyIndexBinaryObjectFactory, valueIndexBinaryObjectFactory);
            return new InternalTreeNode<>(bytes, keyIndexBinaryObjectFactory);
        }
    }


    class ClusterNodeFactory<K extends Comparable<K>> implements NodeFactory<K> {

        private final IndexBinaryObjectFactory<K> keyIndexBinaryObjectFactory;

        public ClusterNodeFactory(IndexBinaryObjectFactory<K> keyIndexBinaryObjectFactory) {
            this.keyIndexBinaryObjectFactory = keyIndexBinaryObjectFactory;
        }

        @Override
        public AbstractTreeNode<K> fromBytes(byte[] bytes) {
            if ((bytes[0] & TYPE_LEAF_NODE_BIT) == TYPE_LEAF_NODE_BIT)
                return new LeafClusterTreeNode<>(bytes, keyIndexBinaryObjectFactory);
            return new InternalTreeNode<>(bytes, keyIndexBinaryObjectFactory);
        }

        @Override
        public AbstractTreeNode<K> fromBytes(byte[] bytes, AbstractTreeNode.Type type) {
            bytes[0] = type.getSign();
            if (type.equals(AbstractTreeNode.Type.LEAF))
                return new LeafClusterTreeNode<>(bytes, keyIndexBinaryObjectFactory);
            return new InternalTreeNode<>(bytes, keyIndexBinaryObjectFactory);
        }
    }

}
