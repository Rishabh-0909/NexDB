package io.github.rishabh0909.nexdb.index;

import io.github.rishabh0909.nexdb.TestParams;
import io.github.rishabh0909.nexdb.utils.FileUtils;
import io.github.rishabh0909.nexdb.context.EngineConfig;
import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.index.DuplicateBitmapIndexManager;
import io.github.rishabh0909.nexdb.index.DuplicateIndexManager;
import io.github.rishabh0909.nexdb.index.UniqueQueryableIndex;
import io.github.rishabh0909.nexdb.index.data.PointerIndexBinaryObject;
import io.github.rishabh0909.nexdb.index.tree.BPlusTreeUniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.serialization.IntegerSerializer;
import io.github.rishabh0909.nexdb.storage.db.DiskPageDatabaseStorageManager;
import io.github.rishabh0909.nexdb.storage.index.IndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.OrganizedFileIndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.header.JsonIndexHeaderManager;
import io.github.rishabh0909.nexdb.storage.pool.FileHandler;
import io.github.rishabh0909.nexdb.storage.pool.UnlimitedFileHandlerPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ListIterator;
import java.util.Optional;

public class DuplicateBitmapIndexManagerTestCase {
    private Path dbPath;
    private EngineConfig engineConfig;
    private DiskPageDatabaseStorageManager diskPageDatabaseStorageManager;

    @BeforeEach
    public void setUp() throws IOException {
        this.dbPath = Files.createTempDirectory("TEST_BinaryListIteratorTestCase");
        this.engineConfig = EngineConfig.builder()
                .baseDBPath(this.dbPath.toString())
                .bTreeDegree(10)
                .build();

        this.diskPageDatabaseStorageManager = new DiskPageDatabaseStorageManager(
                engineConfig,
                new UnlimitedFileHandlerPool(
                        FileHandler.SingletonFileHandlerFactory.getInstance()
                )
        );
    }

    @AfterEach
    public void destroy() throws IOException {
        FileUtils.deleteDirectory(dbPath.toString());
    }

    private UniqueQueryableIndex<Integer, Pointer> getIntegerPointerUniqueTreeIndexManager() {
        IndexStorageManager indexStorageManager = new OrganizedFileIndexStorageManager(new JsonIndexHeaderManager.SingletonFactory(), engineConfig, new UnlimitedFileHandlerPool(FileHandler.SingletonFileHandlerFactory.getInstance()));

        return new BPlusTreeUniqueTreeIndexManager<>(
                1,
                engineConfig.getBTreeDegree(),
                indexStorageManager,
                new IntegerSerializer().getIndexBinaryObjectFactory(TestParams.FAKE_FIELD),
                new PointerIndexBinaryObject.Factory()
        );
    }

    @Test
    public void test_addRemoveAndIterate() throws Exception {
        UniqueQueryableIndex<Integer, Pointer> uniqueTreeIndexManager = getIntegerPointerUniqueTreeIndexManager();

        DuplicateIndexManager<Integer, Integer> duplicateIndexManager = new DuplicateBitmapIndexManager<>(
                1,
                uniqueTreeIndexManager,
                new IntegerSerializer().getIndexBinaryObjectFactory(TestParams.FAKE_FIELD),
                diskPageDatabaseStorageManager
        );

        int[] testItems = new int[]{1,2,3,4,9,10,11,12};

        for (int item : testItems) {
            Assertions.assertTrue(duplicateIndexManager.addIndex(1, item));
        }


        Optional<ListIterator<Integer>> listIteratorOptional =
                duplicateIndexManager.getIndex(1);

        Assertions.assertTrue(listIteratorOptional.isPresent());
        ListIterator<Integer> integerListIterator = listIteratorOptional.get();

        for (int item : testItems) {
            Assertions.assertTrue(integerListIterator.hasNext());
            Assertions.assertEquals(item, integerListIterator.next());
        }


        Assertions.assertTrue(duplicateIndexManager.removeIndex(1, testItems[0]));
        Assertions.assertFalse(duplicateIndexManager.removeIndex(1, 10000));


        listIteratorOptional = duplicateIndexManager.getIndex(1);
        integerListIterator = listIteratorOptional.get();
        for (int item : testItems) {
            if (item == 1)
                continue;
            Assertions.assertTrue(integerListIterator.hasNext());
            Assertions.assertEquals(item, integerListIterator.next());
        }


        duplicateIndexManager.purgeIndex();
        listIteratorOptional = duplicateIndexManager.getIndex(1);
        Assertions.assertTrue(listIteratorOptional.isEmpty());

    }
}
