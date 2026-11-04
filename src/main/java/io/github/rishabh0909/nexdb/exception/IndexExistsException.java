package io.github.rishabh0909.nexdb.exception;

public class IndexExistsException extends InternalOperationException {
    public IndexExistsException(String message) {
        super("Index already exists: " + message);
    }
}
