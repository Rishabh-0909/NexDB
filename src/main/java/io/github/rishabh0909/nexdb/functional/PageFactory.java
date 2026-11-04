package io.github.rishabh0909.nexdb.functional;

import io.github.rishabh0909.nexdb.exception.InternalOperationException;
import io.github.rishabh0909.nexdb.storage.db.Page;
import io.github.rishabh0909.nexdb.storage.db.PageBuffer;

@FunctionalInterface
public interface PageFactory {
    Page apply(PageBuffer.PageTitle t) throws InternalOperationException;
}
