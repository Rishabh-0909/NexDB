package io.github.rishabh0909.nexdb.storage.index;

import io.github.rishabh0909.nexdb.ds.KVSize;
import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.storage.index.header.IndexHeaderManager;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IndexStorageManager {
    CompletableFuture<Optional<NodeData>> getRoot(int indexId, KVSize kvSize) throws InternalOperationException;

    byte[] getEmptyNode(KVSize kvSize);
    default CompletableFuture<NodeData> readNode(int indexId, Pointer pointer, KVSize kvSize) throws InternalOperationException {
        return this.readNode(indexId, pointer.getPosition(), pointer.getChunk(), kvSize);
    }
    CompletableFuture<NodeData> readNode(int indexId, long position, int chunk, KVSize kvSize) throws InternalOperationException;

    CompletableFuture<NodeData> writeNewNode(int indexId, byte[] data, boolean isRoot, KVSize size) throws InternalOperationException;
    default CompletableFuture<NodeData> writeNewNode(int indexId, byte[] data, KVSize size) throws InternalOperationException {
        return this.writeNewNode(indexId, data, false, size);
    }
    default CompletableFuture<Void> updateNode(int indexId, byte[] data, Pointer pointer) throws InternalOperationException {
        return this.updateNode(indexId, data, pointer, false);
    }
    CompletableFuture<Void> updateNode(int indexId, byte[] data, Pointer pointer, boolean root) throws InternalOperationException;

    void close() throws InternalOperationException;

    CompletableFuture<Void> removeNode(int indexId, Pointer pointer, KVSize size) throws InternalOperationException;

    boolean exists(int indexId);

    IndexHeaderManager getIndexHeaderManager();

    default boolean supportsPurge(){return false;}
    default void purgeIndex(int indexId){}

    record NodeData(Pointer pointer, byte[] bytes){}
}
