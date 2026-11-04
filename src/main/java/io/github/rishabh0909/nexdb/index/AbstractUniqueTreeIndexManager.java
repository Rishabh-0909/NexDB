package io.github.rishabh0909.nexdb.index;

public abstract class AbstractUniqueTreeIndexManager<K extends Comparable<K>, V> implements UniqueTreeIndexManager<K,V> {
    protected final int indexId;

    public AbstractUniqueTreeIndexManager(int indexId) {
        this.indexId = indexId;
    }

    @Override
    public int getIndexId() {
        return this.indexId;
    }
}
