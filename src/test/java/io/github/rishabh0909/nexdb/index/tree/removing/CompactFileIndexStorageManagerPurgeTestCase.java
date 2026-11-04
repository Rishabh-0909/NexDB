package io.github.rishabh0909.nexdb.index.tree.removing;

import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.index.UniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.index.tree.BPlusTreeUniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.storage.index.CompactFileIndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.IndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.header.JsonIndexHeaderManager;
import io.github.rishabh0909.nexdb.storage.pool.FileHandler;
import io.github.rishabh0909.nexdb.storage.pool.UnlimitedFileHandlerPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.rishabh0909.nexdb.TestParams.DEFAULT_INDEX_BINARY_OBJECT_FACTORY;

public class CompactFileIndexStorageManagerPurgeTestCase extends BaseBPlusTreeUniqueTreeIndexManagerRemovalTestCase {

    private IndexStorageManager getIndexStorageManager() {
        return new CompactFileIndexStorageManager(
                new JsonIndexHeaderManager.SingletonFactory(),
                engineConfig,
                new UnlimitedFileHandlerPool(FileHandler.SingletonFileHandlerFactory.getInstance())
        );
    }

    private UniqueTreeIndexManager<Long, Long> getIndexManager(IndexStorageManager indexStorageManager) {
        return new BPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get(), DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get());
    }

    @Test
    public void testPurge() throws InternalOperationException {
        IndexStorageManager indexStorageManager = getIndexStorageManager();

        UniqueTreeIndexManager<Long, Long> indexManager = getIndexManager(indexStorageManager);

        for (long i = 1; i < 11; i++) {
            indexManager.addIndex(i, i);
        }

        indexManager.purgeIndex();

        for (long i = 1; i < 10L * degree * BPlusTreeUniqueTreeIndexManager.PURGE_ITERATION_MULTIPLIER; i++) {
            Assertions.assertFalse(indexManager.getIndex(i).isPresent(), "%d is present while it shouldn't be!".formatted(i));
        }

    }

}
