package io.github.rishabh0909.nexdb.storage.index;

import io.github.rishabh0909.nexdb.context.EngineConfig;
import io.github.rishabh0909.nexdb.ds.KVSize;
import io.github.rishabh0909.nexdb.ds.Pointer;
import io.github.rishabh0909.nexdb.exception.ErrorMessage;
import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.storage.index.header.IndexHeaderManager;
import io.github.rishabh0909.nexdb.storage.index.header.IndexHeaderManagerSingletonFactory;
import io.github.rishabh0909.nexdb.storage.pool.FileHandlerPool;
import io.github.rishabh0909.nexdb.storage.pool.ManagedFileHandler;
import io.github.rishabh0909.nexdb.utils.FileUtils;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class CompactFileIndexStorageManager extends BaseFileIndexStorageManager {

    public CompactFileIndexStorageManager(IndexHeaderManagerSingletonFactory indexHeaderManagerSingletonFactory, EngineConfig engineConfig, FileHandlerPool fileHandlerPool) {
        super(null, indexHeaderManagerSingletonFactory, engineConfig, fileHandlerPool);
    }

    public CompactFileIndexStorageManager(IndexHeaderManagerSingletonFactory indexHeaderManagerSingletonFactory, EngineConfig engineConfig) {
        super(indexHeaderManagerSingletonFactory, engineConfig);
    }

    protected Path getIndexFilePath(int indexId, int chunk) {
        return Path.of(path.toString(), String.format("%s.bin", INDEX_FILE_NAME));
    }

    @Override
    protected IndexHeaderManager.Location getIndexBeginningInChunk(int indexId, int chunk) {
        return new IndexHeaderManager.Location(0,0);
    }

    /* Todo:
    *           When nodes with different types of keys and values try to allocate space from the same file
    *       we will face a problem when we want to check if there is empty space at the end of the file
    *           The way checking for space works is by seeing if according the the currently passed `kvSize` there is a node
    *       at a specific byte (checking first byte and see if it equals to any bit flags of a tree node).
    *       This logic is present at getPossibleAllocationLocation() method.
    *           Considering that different kvSizes may be passed here, allocation can simply fail
    *       and a space that is already used by another node (but may be empty for now) may be returned!
    *           Possible workaround is either to track last node position and size, or to change implementation and use PageBuffer
    */
    protected Pointer getAllocatedSpaceForNewNode(int indexId, int chunk, KVSize kvSize) throws InternalOperationException {
        ManagedFileHandler managedFileHandler = this.getManagedFileHandler(indexId, 0);
        try {
            AsynchronousFileChannel asynchronousFileChannel = managedFileHandler.getAsynchronousFileChannel();

            synchronized (this){
                long fileSize = asynchronousFileChannel.size();

                // Check if we have an empty space
                // Todo: in reference to the comment above the method, isnt this if statement a lill dumb?
                //       After second allocation, its always true
                if (fileSize >= this.getIndexGrowthAllocationSize(kvSize)){
                    long positionToCheck = fileSize - this.getIndexGrowthAllocationSize(kvSize);

                    byte[] bytes = FileUtils.readBytes(asynchronousFileChannel, positionToCheck, this.getIndexGrowthAllocationSize(kvSize)).get();
                    Optional<Integer> optionalAdditionalPosition = getPossibleAllocationLocation(bytes, kvSize);
                    if (optionalAdditionalPosition.isPresent()){
                        long finalPosition = positionToCheck + optionalAdditionalPosition.get();
                        managedFileHandler.close();
                        return new Pointer(Pointer.TYPE_NODE, finalPosition, chunk);
                    }

                }

                Long position = FileUtils.allocate(asynchronousFileChannel, this.getIndexGrowthAllocationSize(kvSize)).get();
                managedFileHandler.close();
                return new Pointer(Pointer.TYPE_NODE, position, chunk);
            }
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new InternalOperationException(ErrorMessage.EM_FILE_ALLOCATION, e);
        }


    }

}
