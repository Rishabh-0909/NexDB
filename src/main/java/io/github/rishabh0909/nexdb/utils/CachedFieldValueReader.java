package io.github.rishabh0909.nexdb.utils;

import io.github.rishabh0909.nexdb.exception.DeserializationException;
import io.github.rishabh0909.nexdb.exception.SerializationException;
import io.github.rishabh0909.nexdb.scheme.Scheme;
import io.github.rishabh0909.nexdb.serialization.CollectionSerializationUtil;
import io.github.rishabh0909.nexdb.serialization.Serializer;
import io.github.rishabh0909.nexdb.serialization.SerializerRegistry;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class CachedFieldValueReader {
    private final Map<Scheme.Field, Object> cache = new HashMap<>();
    private final Scheme.Collection collection;
    @Getter
    private final byte[] bytes;

    public CachedFieldValueReader(Scheme.Collection collection, byte[] bytes) {
        this.collection = collection;
        this.bytes = bytes;
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> void write(Scheme.Field field, T value) throws SerializationException {
        cache.put(field, value);
        Serializer<T> serializer = (Serializer<T>) SerializerRegistry.getInstance().getSerializer(field.getType());
        CollectionSerializationUtil.setValueOfField(
                collection,
                field,
                bytes,
                serializer.serialize(value)
        );
    }

    @SuppressWarnings("unchecked")
    public <V> V get(Scheme.Field field) throws DeserializationException {
        if (cache.containsKey(field))
            return (V) cache.get(field);

        V value = CollectionSerializationUtil.getValueOfFieldAsObject(
                collection, field, bytes
        );
        cache.put(field, value);

        return value;
    }
}
