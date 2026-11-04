package io.github.rishabh0909.nexdb.operation;

import io.github.rishabh0909.nexdb.scheme.Scheme;
import io.github.rishabh0909.nexdb.utils.ReaderWriterLock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReaderWriterLockPool {
    private final Map<String, ReaderWriterLock> locks = new ConcurrentHashMap<>();
    private static ReaderWriterLockPool INSTANCE;

    private ReaderWriterLockPool() {}

    public synchronized static ReaderWriterLockPool getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReaderWriterLockPool();
        }
        return INSTANCE;
    }

    public ReaderWriterLock getReaderWriterLock(Scheme scheme, Scheme.Collection collection) {
        return locks.computeIfAbsent("%s.%s".formatted(scheme.getDbName(), collection.getName()), s -> new ReaderWriterLock());
    }
}
