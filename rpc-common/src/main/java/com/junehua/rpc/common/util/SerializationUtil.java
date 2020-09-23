package com.junehua.rpc.common.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author JuneHua
 * @Date 2020/9/22 23:57
 * @Version 1.0
 */
public class SerializationUtil {

    private static final Map<Class<?>, Schema<?>> cache = new ConcurrentHashMap<>();

    private SerializationUtil() {}

    public static <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        Schema<T> schema = getSchemaFromCache(clazz);
        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, linkedBuffer);
        } catch (Exception exception) {
            throw new IllegalArgumentException("序列化错误");
        } finally {
            linkedBuffer.clear();
        }
    }

    private static <T> Schema<T> getSchemaFromCache(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) cache.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            cache.put(clazz, schema);
        }
        return schema;
    }
}
