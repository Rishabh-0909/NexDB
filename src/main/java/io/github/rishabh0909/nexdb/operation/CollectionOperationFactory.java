package io.github.rishabh0909.nexdb.operation;

import io.github.rishabh0909.nexdb.scheme.Scheme;
import io.github.rishabh0909.nexdb.storage.db.DatabaseStorageManager;
import io.github.rishabh0909.nexdb.storage.db.DatabaseStorageManagerSingletonFactory;

public class CollectionOperationFactory {
    protected final Scheme scheme;
    protected final Scheme.Collection collection;
    protected final ReaderWriterLockPool readerWriterLockPool = ReaderWriterLockPool.getInstance();
    protected final CollectionIndexProviderSingletonFactory collectionIndexProviderSingletonFactory;
    protected final DatabaseStorageManagerSingletonFactory databaseStorageManagerSingletonFactory;

    public CollectionOperationFactory(Scheme scheme, Scheme.Collection collection, CollectionIndexProviderSingletonFactory collectionIndexProviderSingletonFactory, DatabaseStorageManagerSingletonFactory databaseStorageManagerSingletonFactory) {
        this.scheme = scheme;
        this.collection = collection;
        this.collectionIndexProviderSingletonFactory = collectionIndexProviderSingletonFactory;
        this.databaseStorageManagerSingletonFactory = databaseStorageManagerSingletonFactory;
    }

    public CollectionOperation create() {
        return new CollectionOperation(collection) {
            private final CollectionIndexProvider collectionIndexProvider = collectionIndexProviderSingletonFactory.getInstance(collection);
            private final DatabaseStorageManager databaseStorageManager = databaseStorageManagerSingletonFactory.getInstance();


            @Override
            public CollectionSelectOperation<?> select() {
                return new DefaultCollectionSelectOperation<>(
                        collection,
                        readerWriterLockPool.getReaderWriterLock(scheme, collection),
                        collectionIndexProvider,
                        databaseStorageManager
                );
            }

            @Override
            public CollectionUpdateOperation<?> update() {
                return new DefaultCollectionUpdateOperation<>(
                        collection,
                        readerWriterLockPool.getReaderWriterLock(scheme, collection),
                        collectionIndexProvider,
                        databaseStorageManager
                );
            }

            @Override
            public CollectionDeleteOperation<?> delete() {
                return new DefaultCollectionDeleteOperation<>(
                        collection,
                        readerWriterLockPool.getReaderWriterLock(scheme, collection),
                        collectionIndexProvider,
                        databaseStorageManager
                );
            }

            @Override
            public CollectionInsertOperation<?> insert() {
                return new DefaultCollectionInsertOperation<>(
                        scheme,
                        collection,
                        readerWriterLockPool.getReaderWriterLock(scheme, collection),
                        collectionIndexProvider,
                        databaseStorageManager
                );
            }
        };
    }
}
