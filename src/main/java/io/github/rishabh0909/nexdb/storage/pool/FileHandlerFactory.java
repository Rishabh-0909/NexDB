package io.github.rishabh0909.nexdb.storage.pool;

import java.io.IOException;

public interface FileHandlerFactory {
    FileHandler getFileHandler(String path) throws IOException;
}
