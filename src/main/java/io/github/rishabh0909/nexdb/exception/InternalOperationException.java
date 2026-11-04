package io.github.rishabh0909.nexdb.exception;

public class InternalOperationException extends Exception {
    private static final String PREFIX = "NexDB core error: ";

    public InternalOperationException() {
        super(PREFIX);
    }

    public InternalOperationException(String message) {
        super(PREFIX + message);
    }

    public InternalOperationException(String message, Throwable cause) {
        super(PREFIX + message, cause);
    }

    public InternalOperationException(Throwable cause) {
        super(cause);
    }

    public InternalOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(PREFIX + message, cause, enableSuppression, writableStackTrace);
    }
}
