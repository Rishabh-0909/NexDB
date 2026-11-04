package io.github.rishabh0909.nexdb.index.tree.removing;

import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.index.UniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.index.tree.BPlusTreeUniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.storage.index.IndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.OrganizedFileIndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.header.JsonIndexHeaderManager;
import io.github.rishabh0909.nexdb.storage.pool.FileHandler;
import io.github.rishabh0909.nexdb.storage.pool.UnlimitedFileHandlerPool;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static io.github.rishabh0909.nexdb.TestParams.LONG_INDEX_BINARY_OBJECT_FACTORY;

public class TestAddRemoveLong extends BaseBPlusTreeUniqueTreeIndexManagerRemovalTestCase {

    protected IndexStorageManager getIndexStorageManager() {
        return new OrganizedFileIndexStorageManager(
                "test",
                new JsonIndexHeaderManager.SingletonFactory(),
                engineConfig,
                new UnlimitedFileHandlerPool(FileHandler.SingletonFileHandlerFactory.getInstance())
        );
    }

    private UniqueTreeIndexManager<Long, Long> getIndexManager(IndexStorageManager indexStorageManager) {
        return new BPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, LONG_INDEX_BINARY_OBJECT_FACTORY.get(), LONG_INDEX_BINARY_OBJECT_FACTORY.get());
    }

    @Test
    public void test() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getIndexStorageManager();
        UniqueTreeIndexManager<Long, Long> indexManager = getIndexManager(indexStorageManager);

        for (long i = 1; i < 11; i++) {
            indexManager.addIndex(i, i);
        }

        for (long i = 1; i < 11; i++) {
            indexManager.removeIndex(i);
        }

    }

}
