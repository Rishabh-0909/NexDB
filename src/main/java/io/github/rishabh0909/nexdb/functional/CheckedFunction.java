package io.github.rishabh0909.nexdb.functional;

@FunctionalInterface
public interface CheckedFunction<I, O, E extends Exception> {
    O apply(I in) throws E;
}
