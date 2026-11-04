package io.github.rishabh0909.nexdb.serialization;

import io.github.rishabh0909.nexdb.exception.DeserializationException;
import io.github.rishabh0909.nexdb.exception.SerializationException;
import io.github.rishabh0909.nexdb.index.data.IndexBinaryObjectFactory;
import io.github.rishabh0909.nexdb.scheme.Scheme;
import com.google.common.primitives.UnsignedLong;

import java.math.BigInteger;
import java.util.List;

import static io.github.rishabh0909.nexdb.utils.BinaryUtils.toByteArray;

public class UnsignedLongSerializer implements Serializer<UnsignedLong> {
    @Override
    public Class<UnsignedLong> getType() {
        return UnsignedLong.class;
    }

    @Override
    public String typeName() {
        return FieldType.UNSIGNED_LONG.getName();
    }

    @Override
    public List<String> compatibleTypes() {
        return List.of(FieldType.LONG.getName(), FieldType.INT.getName());
    }

    @Override
    public int maxSize() {
        return Long.BYTES;
    }

    @Override
    public int minSize() {
        return Long.BYTES;
    }

    @Override
    public byte[] serialize(UnsignedLong unsignedLong, Scheme.Meta meta) throws SerializationException {
        return toByteArray(unsignedLong);
    }

    @Override
    public UnsignedLong deserialize(byte[] bytes, Scheme.Meta meta) throws DeserializationException {
        BigInteger bigInteger = new BigInteger(1, bytes);
        return UnsignedLong.valueOf(bigInteger);
    }

    @Override
    public int getSize(Scheme.Meta meta) {
        return Long.BYTES;
    }

    @Override
    public byte[] serializeDefault(String defaultValue, Scheme.Meta meta) throws SerializationException {
        return new byte[Long.BYTES]; // Todo
    }

    @Override
    public IndexBinaryObjectFactory<UnsignedLong> getIndexBinaryObjectFactory(Scheme.Field field) {
        return new SerializerIndexBinaryObjectFactory<>(this, field, () -> UnsignedLong.valueOf(1L), i -> i.plus(UnsignedLong.ONE));
    }
}
