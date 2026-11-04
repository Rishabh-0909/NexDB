package io.github.rishabh0909.nexdb.storage.db;

import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.functional.DBObjectUpdateConsumer;

import java.util.Optional;


public interface DatabaseStorageManager {
    Pointer store(int scheme, int collectionId, int version, byte[] data) throws InternalOperationException;
    void update(Pointer pointer, DBObjectUpdateConsumer<DBObject> dbObjectConsumer) throws InternalOperationException;
    void update(Pointer pointer, byte[] bytes) throws InternalOperationException;
    Optional<DBObject> select(Pointer pointer) throws InternalOperationException;
    void remove(Pointer pointer) throws InternalOperationException;
    default void close(){}
}
