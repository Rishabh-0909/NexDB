package io.github.rishabh0909.nexdb.index.tree.reading;

import io.github.rishabh0909.nexdb.utils.FileUtils;
import io.github.rishabh0909.nexdb.context.EngineConfig;
import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.index.CachedUniqueQueryableIndexDecorator;
import io.github.rishabh0909.nexdb.index.UniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.index.tree.node.cluster.ClusterBPlusTreeUniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.storage.index.BTreeSizeCalculator;
import io.github.rishabh0909.nexdb.storage.index.CachedIndexStorageManagerDecorator;
import io.github.rishabh0909.nexdb.storage.index.IndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.OrganizedFileIndexStorageManager;
import io.github.rishabh0909.nexdb.storage.index.header.JsonIndexHeaderManager;
import io.github.rishabh0909.nexdb.storage.pool.FileHandler;
import io.github.rishabh0909.nexdb.storage.pool.UnlimitedFileHandlerPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static io.github.rishabh0909.nexdb.TestParams.DEFAULT_INDEX_BINARY_OBJECT_FACTORY;
import static io.github.rishabh0909.nexdb.storage.index.OrganizedFileIndexStorageManager.INDEX_FILE_NAME;

public class CacheBPlusTreeUniqueTreeIndexManagerReadingTestCase {
    private Path dbPath;
    private EngineConfig engineConfig;
    private final int degree = 4;
    private Path indexPath;

    @BeforeEach
    public void setUp() throws IOException {
        dbPath = Files.createTempDirectory("TEST_CacheBPlusTreeIndexManagerReadingTestCase");
        engineConfig = EngineConfig.builder()
                .baseDBPath(dbPath.toString())
                .bTreeDegree(degree)
                .bTreeGrowthNodeAllocationCount(2)
                .build();
        engineConfig.setBTreeMaxFileSize(12L * BTreeSizeCalculator.getClusteredBPlusTreeSize(degree, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get().size()));

        byte[] writingBytes = new byte[]{};
        indexPath = Path.of(dbPath.toString(), String.format("%s.%d", INDEX_FILE_NAME, 0));
        Files.write(indexPath, writingBytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    public void destroy() throws IOException {
        FileUtils.deleteDirectory(dbPath.toString());
    }

    private OrganizedFileIndexStorageManager getStorageManager() throws IOException, ExecutionException, InterruptedException {
        return new OrganizedFileIndexStorageManager(
                "test",
                new JsonIndexHeaderManager.SingletonFactory(),
                engineConfig,
                new UnlimitedFileHandlerPool(FileHandler.SingletonFileHandlerFactory.getInstance())
        );
    }

    @Test
    @Timeout(value = 2)
    public void findIndexSuccessfully_cachedStorage() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getStorageManager();
        indexStorageManager = new CachedIndexStorageManagerDecorator(indexStorageManager, 30);

        UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = new ClusterBPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get());
        Pointer dataPointer = new Pointer(Pointer.TYPE_DATA, 100, 0);

        for (long i = 0; i < 20; i++){
            uniqueTreeIndexManager.addIndex(i, dataPointer);
        }
        Optional<Pointer> optionalPointer = uniqueTreeIndexManager.getIndex(10L);

        Assertions.assertTrue(optionalPointer.isPresent());
        Assertions.assertEquals(dataPointer, optionalPointer.get());

        optionalPointer = uniqueTreeIndexManager.getIndex(10L);
        Assertions.assertEquals(dataPointer, optionalPointer.get());

        indexStorageManager.close();
        destroy();
        setUp();

        indexStorageManager = getStorageManager();
        indexStorageManager = new CachedIndexStorageManagerDecorator(indexStorageManager, 12);

        uniqueTreeIndexManager = new ClusterBPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get());
        UniqueTreeIndexManager<Long, Pointer> finalUniqueTreeIndexManager = uniqueTreeIndexManager;

        Optional<Pointer> index = finalUniqueTreeIndexManager.getIndex(10L);
        Assertions.assertTrue(index.isEmpty());  // Well, we removed root from header, so we get empty optional there already

        indexStorageManager.close();
    }

    @Test
    @Timeout(value = 2)
    public void findIndexFailure_cachedStorage() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getStorageManager();
        indexStorageManager = new CachedIndexStorageManagerDecorator(indexStorageManager, 12);

        UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = new ClusterBPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get());
        Pointer dataPointer = new Pointer(Pointer.TYPE_DATA, 100, 0);

        for (long i = 0; i < 20; i++){
            uniqueTreeIndexManager.addIndex(i, dataPointer);
        }

        Optional<Pointer> optionalPointer = uniqueTreeIndexManager.getIndex(100L);
        Assertions.assertFalse(optionalPointer.isPresent());

        // Removing file and checking if we can still find index
        destroy();
        optionalPointer = uniqueTreeIndexManager.getIndex(100L);
        Assertions.assertFalse(optionalPointer.isPresent());

        indexStorageManager.close();
    }

    @Test
    @Timeout(value = 2)
    public void findIndexSuccessfully_cachedIndexManager() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getStorageManager();

        UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = new CachedUniqueQueryableIndexDecorator<>(
                new ClusterBPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get()),
                20
        );
        Pointer dataPointer = new Pointer(Pointer.TYPE_DATA, 100, 0);

        for (long i = 1; i < 20; i++){
            uniqueTreeIndexManager.addIndex(i, dataPointer);
        }
        Optional<Pointer> optionalPointer = uniqueTreeIndexManager.getIndex(10L);

        Assertions.assertTrue(optionalPointer.isPresent());
        Assertions.assertEquals(dataPointer, optionalPointer.get());

        destroy();
        optionalPointer = uniqueTreeIndexManager.getIndex(10L);
        Assertions.assertTrue(optionalPointer.isPresent());
        Assertions.assertEquals(dataPointer, optionalPointer.get());

        indexStorageManager.close();
    }

    @Test
    @Timeout(value = 2)
    public void findIndexFailure_cachedIndexManager() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getStorageManager();
        UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = new CachedUniqueQueryableIndexDecorator<>(
                new ClusterBPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get()),
                20
        );
        Pointer dataPointer = new Pointer(Pointer.TYPE_DATA, 100, 0);

        for (long i = 1; i < 20; i++){
            uniqueTreeIndexManager.addIndex(i, dataPointer);
        }

        Optional<Pointer> optionalPointer = uniqueTreeIndexManager.getIndex(100L);
        Assertions.assertFalse(optionalPointer.isPresent());

        destroy();
        optionalPointer = uniqueTreeIndexManager.getIndex(100L);
        Assertions.assertFalse(optionalPointer.isPresent());

        indexStorageManager.close();
    }
}
