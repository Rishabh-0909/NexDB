package io.github.rishabh0909.nexdb.index.tree.storing;

import io.github.rishabh0909.nexdb.utils.FileUtils;
import io.github.rishabh0909.nexdb.context.EngineConfig;
import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.index.UniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.index.data.PointerIndexBinaryObject;
import io.github.rishabh0909.nexdb.index.tree.node.AbstractLeafTreeNode;
import io.github.rishabh0909.nexdb.index.tree.node.AbstractTreeNode;
import io.github.rishabh0909.nexdb.index.tree.node.cluster.ClusterBPlusTreeUniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.storage.index.BTreeSizeCalculator;
import io.github.rishabh0909.nexdb.storage.index.CompactFileIndexStorageManager;
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
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.github.rishabh0909.nexdb.TestParams.DEFAULT_INDEX_BINARY_OBJECT_FACTORY;
import static io.github.rishabh0909.nexdb.storage.index.CompactFileIndexStorageManager.INDEX_FILE_NAME;

public class MultiTableBPlusTreeIndexManagerCompactStorageManagerTestCase {
    private Path dbPath;
    private EngineConfig engineConfig;
    private final int degree = 4;

    @BeforeEach
    public void setUp() throws IOException {
        dbPath = Files.createTempDirectory("TEST_MultiTableBPlusTreeIndexManagerSingleStorageManagerTestCase");
        engineConfig = EngineConfig.builder()
                .baseDBPath(dbPath.toString())
                .bTreeDegree(degree)
                .bTreeGrowthNodeAllocationCount(10)
                .build();
        engineConfig.setBTreeMaxFileSize(2 * 15L * BTreeSizeCalculator.getClusteredBPlusTreeSize(degree, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get().size()));

        byte[] writingBytes = new byte[2 * 13 * BTreeSizeCalculator.getClusteredBPlusTreeSize(degree, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get().size())];
        Path indexPath = Path.of(dbPath.toString(), String.format("%s.bin", INDEX_FILE_NAME));
        Files.write(indexPath, writingBytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    @AfterEach
    public void destroy() throws IOException {
        FileUtils.deleteDirectory(dbPath.toString());
    }

    private CompactFileIndexStorageManager getSingleFileIndexStorageManager() throws IOException, ExecutionException, InterruptedException {
        return new CompactFileIndexStorageManager(
                new JsonIndexHeaderManager.SingletonFactory(),
                engineConfig,
                new UnlimitedFileHandlerPool(FileHandler.SingletonFileHandlerFactory.getInstance())
        );
    }


    /**
     *
     * The B+Tree in this test will include numbers from [1-12] added respectively
     * The shape of the tree will be like below, and the test verifies that
     * The test validates the tree for 2 tables in same database
     * 007
     * ├── .
     * │   ├── 001
     * │   └── 002
     * ├── 003
     * │   ├── 003
     * │   └── 004
     * ├── 005
     * │   ├── 005
     * │   └── 006
     * ├── .
     * │   ├── 007
     * │   └── 008
     * ├── 009
     * │   ├── 009
     * │   └── 010
     * └── 0011
     *     ├── 011
     *     └── 012
     */
    @Test
    public void testMultiSplitAddIndex() throws IOException, ExecutionException, InterruptedException, InternalOperationException {

        List<Long> testIdentifiers = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L);
        Pointer samplePointer = new Pointer(Pointer.TYPE_DATA, 100, 0);

        for (int tableId = 1; tableId <= 2; tableId++){
            CompactFileIndexStorageManager compactFileIndexStorageManager = getSingleFileIndexStorageManager();
            UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = new ClusterBPlusTreeUniqueTreeIndexManager<>(tableId, degree, compactFileIndexStorageManager, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get());



            AbstractTreeNode<Long> lastTreeNode = null;
            for (long testIdentifier : testIdentifiers) {
                lastTreeNode = uniqueTreeIndexManager.addIndex(testIdentifier, samplePointer);
            }

            Assertions.assertTrue(lastTreeNode.isLeaf());
            Assertions.assertEquals(2, lastTreeNode.getKeyList(degree, PointerIndexBinaryObject.BYTES).size());
            Assertions.assertEquals(samplePointer.getPosition(), ((AbstractLeafTreeNode<Long, Pointer>) lastTreeNode).getKeyValues(degree).next().value().getPosition());

            StoredTreeStructureVerifier.testOrderedTreeStructure(compactFileIndexStorageManager, tableId, 1, degree);
        }

    }


    /**
     *
     * The B+Tree in this test will include numbers from [1-12] added respectively
     * The shape of the tree will be like below, and the test verifies that
     * The test validates the tree for 2 tables in same database
     * 009
     * ├── .
     * │   ├── 001
     * │   └── 002
     * ├── 003
     * │   ├── 003
     * │   ├── 004
     * │   └── 005
     * ├── 006
     * │   ├── 006
     * │   ├── 007
     * │   └── 008
     * ├── .
     * │   ├── 009
     * │   └── 010
     * └── 011
     *     ├── 011
     *     └── 012
     */
    @Test
    public void testMultiSplitAddIndex2() throws IOException, ExecutionException, InterruptedException, InternalOperationException {

        List<Long> testIdentifiers = Arrays.asList(1L, 4L, 9L, 6L, 10L, 8L, 3L, 2L, 11L, 5L, 7L, 12L);
        Pointer samplePointer = new Pointer(Pointer.TYPE_DATA, 100, 0);
        CompactFileIndexStorageManager compactFileIndexStorageManager = getSingleFileIndexStorageManager();

        for (int tableId = 1; tableId <= 2; tableId++){
            UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = new ClusterBPlusTreeUniqueTreeIndexManager<>(tableId, degree, compactFileIndexStorageManager, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get());

            AbstractTreeNode<Long> lastTreeNode = null;
            for (long testIdentifier : testIdentifiers) {
                lastTreeNode = uniqueTreeIndexManager.addIndex(testIdentifier, samplePointer);
            }

            Assertions.assertTrue(lastTreeNode.isLeaf());
            Assertions.assertEquals(2, lastTreeNode.getKeyList(degree, PointerIndexBinaryObject.BYTES).size());
            Assertions.assertEquals(samplePointer.getPosition(), ((AbstractLeafTreeNode<Long, Pointer>) lastTreeNode).getKeyValues(degree).next().value().getPosition());

            StoredTreeStructureVerifier.testUnOrderedTreeStructure1(compactFileIndexStorageManager, tableId, 1, degree);

        }

    }

}
