package io.github.rishabh0909.nexdb.exception;

public class VerificationException extends InternalOperationException {
    public static final String PREFIX = "Verification Error: ";

    public VerificationException(String message) {
        super(PREFIX + message);
    }
}
