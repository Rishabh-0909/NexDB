package io.github.rishabh0909.nexdb;

import io.github.rishabh0909.nexdb.ds.KVSize;
import io.github.rishabh0909.nexdb.index.data.IndexBinaryObjectFactory;
import io.github.rishabh0909.nexdb.index.data.PointerIndexBinaryObject;
import io.github.rishabh0909.nexdb.scheme.Scheme;
import io.github.rishabh0909.nexdb.serialization.LongSerializer;
import io.github.rishabh0909.nexdb.serialization.UnsignedLongSerializer;
import com.google.common.primitives.UnsignedLong;

import java.util.function.Supplier;

public class TestParams {
    public static final KVSize DEFAULT_KV_SIZE =  new KVSize(Long.BYTES, PointerIndexBinaryObject.BYTES);
    public static final Supplier<Scheme.Field> FAKE_FIELD_SUPPLIER = () -> Scheme.Field.builder().build();
    public static final Scheme.Field FAKE_FIELD = FAKE_FIELD_SUPPLIER.get();
    public static final Supplier<IndexBinaryObjectFactory<Long>> LONG_INDEX_BINARY_OBJECT_FACTORY = () -> new LongSerializer().getIndexBinaryObjectFactory(FAKE_FIELD);
    public static final Supplier<IndexBinaryObjectFactory<Long>> DEFAULT_INDEX_BINARY_OBJECT_FACTORY = () -> new LongSerializer().getIndexBinaryObjectFactory(FAKE_FIELD);
    public static final Supplier<IndexBinaryObjectFactory<UnsignedLong>> ULONG_INDEX_BINARY_OBJECT_FACTORY = () -> new UnsignedLongSerializer().getIndexBinaryObjectFactory(FAKE_FIELD);
}
