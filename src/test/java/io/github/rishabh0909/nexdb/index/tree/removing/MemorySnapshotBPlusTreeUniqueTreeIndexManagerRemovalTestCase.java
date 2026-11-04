package io.github.rishabh0909.nexdb.index.tree.removing;

import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.index.UniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.index.tree.node.cluster.ClusterBPlusTreeUniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.storage.index.IndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.session.MemorySnapshotIndexIOSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static io.github.rishabh0909.nexdb.TestParams.DEFAULT_INDEX_BINARY_OBJECT_FACTORY;


/* Identical copy of BPlusTreeIndexManagerRemovalTestCase */
public class MemorySnapshotBPlusTreeUniqueTreeIndexManagerRemovalTestCase extends BPlusTreeUniqueTreeIndexManagerRemovalTestCase {

    @Override
    protected UniqueTreeIndexManager<Long, Pointer> getIndexManager(IndexStorageManager indexStorageManager) {
        return new ClusterBPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, MemorySnapshotIndexIOSession.Factory.getInstance(), DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get());
    }

    @Test
    @Timeout(2)
    @Override
    public void testRemovingLeftToRight() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        super.testRemovingLeftToRight();
    }


    @Test
    @Timeout(2)
    @Override
    public void testRemovingRightToLeft() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        super.testRemovingRightToLeft();
    }

    @Test
    @Timeout(2)
    @Override
    public void testRemovingRoot() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        super.testRemovingRoot();
    }
}
