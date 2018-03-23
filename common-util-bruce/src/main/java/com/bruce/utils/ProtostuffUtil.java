package com.bruce.utils;

import com.bruce.protostuff.runtime.RuntimeSchema;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;

import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffUtil {

	//没有缓存的形式
	private static ConcurrentHashMap<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();
	//有缓存的形式
	//private static ConcurrentHashMap<Class<?>, SoftReference<Schema<?>>> cachedSchema = new ConcurrentHashMap<>();

	//有缓存的形式
	/*private static <T> Schema<T> getSchema(Class<T> clazz) {
		SoftReference<Schema<?>> schemaSoftReference = cachedSchema.get(clazz);
		@SuppressWarnings("unchecked")
		Schema<T> schema = schemaSoftReference != null ? (Schema<T>) schemaSoftReference.get() : null;
		if (schema == null) {
			schema = RuntimeSchema.getSchema(clazz);
			if (schema != null) {
				System.out.println("生成schema");
				cachedSchema.put(clazz, new SoftReference<>(schema));
			}
		}
		return schema;
	}*/

	//没有缓存的形式
	private static <T> Schema<T> getSchema(Class<T> clazz) {
		@SuppressWarnings("unchecked")
		Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
		if (schema == null) {
			schema = RuntimeSchema.getSchema(clazz);
			if (schema != null) {
				System.out.println(clazz.getName()+"生成schema");
				cachedSchema.put(clazz, schema);
			}
		}
		return schema;
	}

	public static <T> T newInstance(Class<T> clas) {
		Schema<T> createFrom = getSchema(clas);
		return createFrom.newMessage();
	}

	public static <T> byte[] serializer(T obj) {
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) obj.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		byte[] byteArray = null;
		try {
			Schema<T> schema = getSchema(clazz);
			byteArray = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
			schema = null;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			buffer.clear();
		}
		return byteArray;
	}

	public static <T> T deserializer(byte[] data, Class<T> clazz) {
		try {
			T obj = clazz.newInstance();
			Schema<T> schema = getSchema(clazz);
			ProtostuffIOUtil.mergeFrom(data, obj, schema);
			schema = null;
			return obj;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}

