package io.github.rishabh0909.nexdb.index.tree.removing;

import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.index.UniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.index.tree.node.cluster.ClusterBPlusTreeUniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.storage.index.IndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.OrganizedFileIndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.header.JsonIndexHeaderManager;
import io.github.rishabh0909.nexdb.storage.pool.FileHandler;
import io.github.rishabh0909.nexdb.storage.pool.UnlimitedFileHandlerPool;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static io.github.rishabh0909.nexdb.TestParams.DEFAULT_INDEX_BINARY_OBJECT_FACTORY;

public class BPlusTreeUniqueTreeIndexManagerRemovalTestCase extends BaseBPlusTreeUniqueTreeIndexManagerRemovalTestCase {

    protected IndexStorageManager getIndexStorageManager() throws IOException, ExecutionException, InterruptedException {
        return new OrganizedFileIndexStorageManager(
                "test",
                new JsonIndexHeaderManager.SingletonFactory(),
                engineConfig,
                new UnlimitedFileHandlerPool(FileHandler.SingletonFileHandlerFactory.getInstance())
        );
    }

    protected UniqueTreeIndexManager<Long, Pointer> getIndexManager(IndexStorageManager indexStorageManager) {
        return new ClusterBPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get());
    }

    @Test
    public void testRemovingLeftToRight() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getIndexStorageManager();
        UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = getIndexManager(indexStorageManager);
        super.testRemovingLeftToRight(uniqueTreeIndexManager, indexStorageManager);
    }

    @Test
    public void testRemovingRightToLeft() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getIndexStorageManager();
        UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = getIndexManager(indexStorageManager);
        super.testRemovingRightToLeft(uniqueTreeIndexManager, indexStorageManager);
    }


    @Test
    public void testRemovingRoot() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getIndexStorageManager();
        UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = getIndexManager(indexStorageManager);
        super.testRemovingRoot(uniqueTreeIndexManager, indexStorageManager);
    }

}
