package io.github.rishabh0909.nexdb.operation;

import io.github.rishabh0909.nexdb.utils.FileUtils;
import io.github.rishabh0909.nexdb.context.EngineConfig;
import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.exception.DeserializationException;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.exception.SerializationException;
import io.github.rishabh0909.nexdb.index.DuplicateQueryableIndex;
import io.github.rishabh0909.nexdb.index.UniqueQueryableIndex;
import io.github.rishabh0909.nexdb.index.UniqueTreeIndexManager;
import io.github.rishabh0909.nexdb.operation.*;
import io.github.rishabh0909.nexdb.operation.query.*;
import io.github.rishabh0909.nexdb.scheme.ModelToCollectionConverter;
import io.github.rishabh0909.nexdb.scheme.Scheme;
import io.github.rishabh0909.nexdb.scheme.annotation.Collection;
import io.github.rishabh0909.nexdb.scheme.annotation.Field;
import io.github.rishabh0909.nexdb.scheme.annotation.Index;
import io.github.rishabh0909.nexdb.serialization.ModelSerializer;
import io.github.rishabh0909.nexdb.storage.db.DatabaseStorageManager;
import io.github.rishabh0909.nexdb.storage.db.DatabaseStorageManagerSingletonFactory;
import io.github.rishabh0909.nexdb.storage.index.DefaultIndexStorageManagerSingletonFactory;
import io.github.rishabh0909.nexdb.storage.index.IndexStorageManagerSingletonFactory;
import io.github.rishabh0909.nexdb.storage.index.header.JsonIndexHeaderManager;
import io.github.rishabh0909.nexdb.storage.pool.FileHandlerPoolSingletonFactory;
import io.github.rishabh0909.nexdb.utils.ReaderWriterLock;
import lombok.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DefaultCollectionSelectOperationTestCase {

    private EngineConfig engineConfig;
    private Path dbPath;
    private final Scheme scheme = Scheme.builder()
            .dbName("test")
            .version(1)
            .build();

    private FileHandlerPoolSingletonFactory fileHandlerPoolSingletonFactory;

    @BeforeEach
    public void setUp() throws IOException {
        this.dbPath = Files.createTempDirectory(this.getClass().getSimpleName());
        this.engineConfig = EngineConfig.builder()
                .clusterKeyType(EngineConfig.ClusterKeyType.LONG)
                .baseDBPath(this.dbPath.toString())
                .build();
        this.fileHandlerPoolSingletonFactory = new FileHandlerPoolSingletonFactory.DefaultFileHandlerPoolSingletonFactory(engineConfig);
    }

    private DatabaseStorageManagerSingletonFactory getDatabaseStorageManagerFactory() {
        return new DatabaseStorageManagerSingletonFactory.DiskPageDatabaseStorageManagerSingletonFactory(engineConfig, fileHandlerPoolSingletonFactory);
    }

    @AfterEach
    public void destroy() throws IOException {
        FileUtils.deleteDirectory(dbPath.toString());
    }


    @Data
    @Collection(id = 1, name = "test")
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestModel {
        @Field(id = 1)
        @Index(primary = true, autoIncrement = true)
        private Integer id;

        @Field(id = 2, maxLength = 20)
        private String name;

        @Field(id = 3)
        @Index()
        private Long age;

        @Field(id = 4, name = "country_code")
        @Index(lowCardinality = true)
        private String country;

    }


    @Test
    public void test() throws IOException, SerializationException, ExecutionException, InterruptedException, InternalOperationException, DeserializationException {
        DatabaseStorageManagerSingletonFactory databaseStorageManagerSingletonFactory = getDatabaseStorageManagerFactory();
        DatabaseStorageManager storageManager = databaseStorageManagerSingletonFactory.getInstance();
        IndexStorageManagerSingletonFactory indexStorageManagerSingletonFactory = new DefaultIndexStorageManagerSingletonFactory(this.engineConfig, new JsonIndexHeaderManager.SingletonFactory(), fileHandlerPoolSingletonFactory, databaseStorageManagerSingletonFactory);
        CollectionIndexProviderSingletonFactory collectionIndexProviderSingletonFactory = new DefaultCollectionIndexProviderSingletonFactory(scheme, engineConfig, indexStorageManagerSingletonFactory, storageManager);

        Scheme.Collection collection = new ModelToCollectionConverter(TestModel.class).toCollection();
        CollectionIndexProvider collectionIndexProvider = collectionIndexProviderSingletonFactory.getInstance(collection);

        TestModel testModel1 = TestModel.builder().id(1).age(10L).country("DE").name("John").build();
        TestModel testModel2 = TestModel.builder().id(2).age(20L).country("FR").name("Rose").build();
        TestModel testModel3 = TestModel.builder().id(3).age(30L).country("USA").name("Jack").build();
        TestModel testModel4 = TestModel.builder().id(4).age(40L).country("GB").name("Foo").build();

        long i = 1;
        for (TestModel testModel : Arrays.asList(testModel1, testModel2, testModel3, testModel4)) {
            ModelSerializer modelSerializer = new ModelSerializer(testModel);
            byte[] serialize = modelSerializer.serialize();

            Pointer pointer = storageManager.store(scheme.getId(), collection.getId(), 1, serialize);

            UniqueTreeIndexManager<Long, Pointer> clusterIndexManager = (UniqueTreeIndexManager<Long, Pointer>) collectionIndexProvider.getClusterIndexManager();
            clusterIndexManager.addIndex(i, pointer);

            UniqueQueryableIndex<Integer, Long> idIndexManager = (UniqueQueryableIndex<Integer, Long>) collectionIndexProvider.getUniqueIndexManager("id");
            idIndexManager.addIndex(testModel.getId(), i);

            DuplicateQueryableIndex<Long, Long> ageIndexManager = (DuplicateQueryableIndex<Long, Long>) collectionIndexProvider.getDuplicateIndexManager("age");
            ageIndexManager.addIndex(testModel.getAge(), i);

            DuplicateQueryableIndex<String, Long> countryIndexManager = (DuplicateQueryableIndex<String, Long>) collectionIndexProvider.getDuplicateIndexManager("country_code");
            countryIndexManager.addIndex(testModel.getCountry(), i);


            i++;
        }


        CollectionSelectOperation<Long> collectionSelectOperation = new DefaultCollectionSelectOperation<>(collection, new ReaderWriterLock(), collectionIndexProviderSingletonFactory.getInstance(collection), storageManager);
        long count = collectionSelectOperation.count();
        Assertions.assertEquals(4L, count);
        Iterator<TestModel> execute = collectionSelectOperation.execute(TestModel.class);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel1);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel2);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel3);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel4);
        Assertions.assertFalse(execute.hasNext());

        execute = collectionSelectOperation.query(
                new Query().where(new SimpleCondition<>("age", Operation.GT, 10L))
        ).execute(TestModel.class);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel2);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel3);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel4);
        Assertions.assertFalse(execute.hasNext());
    }

    @Test
    public void unorderedData() throws IOException, SerializationException, ExecutionException, InterruptedException, InternalOperationException, DeserializationException {
        DatabaseStorageManagerSingletonFactory databaseStorageManagerSingletonFactory = getDatabaseStorageManagerFactory();
        DatabaseStorageManager storageManager = databaseStorageManagerSingletonFactory.getInstance();
        IndexStorageManagerSingletonFactory indexStorageManagerSingletonFactory = new DefaultIndexStorageManagerSingletonFactory(this.engineConfig, new JsonIndexHeaderManager.SingletonFactory(), fileHandlerPoolSingletonFactory, databaseStorageManagerSingletonFactory);

        Scheme.Collection collection = new ModelToCollectionConverter(TestModel.class).toCollection();

        CollectionIndexProviderSingletonFactory collectionIndexProviderSingletonFactory = new DefaultCollectionIndexProviderSingletonFactory(scheme, engineConfig, indexStorageManagerSingletonFactory, storageManager);
        CollectionIndexProvider collectionIndexProvider = collectionIndexProviderSingletonFactory.getInstance(collection);

        TestModel testModel1 = TestModel.builder().id(1).age(30L).country("DE").name("John").build();
        TestModel testModel2 = TestModel.builder().id(2).age(20L).country("FR").name("Rose").build();
        TestModel testModel3 = TestModel.builder().id(3).age(40L).country("USA").name("Jack").build();
        TestModel testModel4 = TestModel.builder().id(4).age(10L).country("GB").name("Foo").build();

        long i = 1;
        for (TestModel testModel : Arrays.asList(testModel1, testModel2, testModel3, testModel4)) {
            ModelSerializer modelSerializer = new ModelSerializer(testModel);
            byte[] serialize = modelSerializer.serialize();

            Pointer pointer = storageManager.store(scheme.getId(), collection.getId(), 1, serialize);

            UniqueTreeIndexManager<Long, Pointer> clusterIndexManager = (UniqueTreeIndexManager<Long, Pointer>) collectionIndexProvider.getClusterIndexManager();
            clusterIndexManager.addIndex(i, pointer);

            UniqueQueryableIndex<Integer, Long> idIndexManager = (UniqueQueryableIndex<Integer, Long>) collectionIndexProvider.getUniqueIndexManager("id");
            idIndexManager.addIndex(testModel.getId(), i);

            DuplicateQueryableIndex<Long, Long> ageIndexManager = (DuplicateQueryableIndex<Long, Long>) collectionIndexProvider.getDuplicateIndexManager("age");
            ageIndexManager.addIndex(testModel.getAge(), i);

            DuplicateQueryableIndex<String, Long> countryIndexManager = (DuplicateQueryableIndex<String, Long>) collectionIndexProvider.getDuplicateIndexManager("country_code");
            countryIndexManager.addIndex(testModel.getCountry(), i);


            i++;
        }


        CollectionSelectOperation<Long> collectionSelectOperation = new DefaultCollectionSelectOperation<>(collection, new ReaderWriterLock(), collectionIndexProviderSingletonFactory.getInstance(collection), storageManager);
        long count = collectionSelectOperation.count();
        Assertions.assertEquals(4L, count);
        Iterator<TestModel> execute = collectionSelectOperation.execute(TestModel.class);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel1);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel2);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel3);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel4);
        Assertions.assertFalse(execute.hasNext());

        execute = collectionSelectOperation.query(
                new Query().sort(new SortField("age", Order.ASC))
        ).execute(TestModel.class);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel4);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel2);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel1);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(execute.next(), testModel3);
        Assertions.assertFalse(execute.hasNext());


        execute = collectionSelectOperation.query(
                new Query()
                        .where(new SimpleCondition<>("age", Operation.GTE, 10L))
                        .sort(new SortField("country_code", Order.ASC))
        ).execute(TestModel.class);
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(testModel1, execute.next());
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(testModel2, execute.next());
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(testModel4, execute.next());
        Assertions.assertTrue(execute.hasNext());
        Assertions.assertEquals(testModel3, execute.next());
        Assertions.assertFalse(execute.hasNext());

    }

    @Test
    public void notEqualQuery() throws SerializationException, InternalOperationException, DeserializationException {
        DatabaseStorageManagerSingletonFactory databaseStorageManagerSingletonFactory = getDatabaseStorageManagerFactory();
        DatabaseStorageManager storageManager = databaseStorageManagerSingletonFactory.getInstance();
        IndexStorageManagerSingletonFactory indexStorageManagerSingletonFactory = new DefaultIndexStorageManagerSingletonFactory(this.engineConfig, new JsonIndexHeaderManager.SingletonFactory(), fileHandlerPoolSingletonFactory, databaseStorageManagerSingletonFactory);

        Scheme.Collection collection = new ModelToCollectionConverter(TestModel.class).toCollection();

        CollectionIndexProviderSingletonFactory collectionIndexProviderSingletonFactory = new DefaultCollectionIndexProviderSingletonFactory(scheme, engineConfig, indexStorageManagerSingletonFactory, storageManager);
        CollectionIndexProvider collectionIndexProvider = collectionIndexProviderSingletonFactory.getInstance(collection);

        TestModel testModel1 = TestModel.builder().id(1).age(30L).country("DE").name("John").build();
        TestModel testModel2 = TestModel.builder().id(2).age(40L).country("FR").name("Rose").build();

        CollectionInsertOperation<Long> collectionInsertOperation = new DefaultCollectionInsertOperation<>(scheme, collection, new ReaderWriterLock(), collectionIndexProviderSingletonFactory.getInstance(collection), storageManager);
        collectionInsertOperation.execute(testModel1);
        collectionInsertOperation.execute(testModel2);


        CollectionSelectOperation<Long> collectionSelectOperation = new DefaultCollectionSelectOperation<>(collection, new ReaderWriterLock(), collectionIndexProviderSingletonFactory.getInstance(collection), storageManager);


        List<TestModel> list = collectionSelectOperation.query(new Query("country_code", Operation.NEQ, "DE")).asList(TestModel.class);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(testModel2, list.getFirst());

        list = collectionSelectOperation.query(new Query("country_code", Operation.NEQ, "FR")).asList(TestModel.class);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(testModel1, list.getFirst());

    }

}
