package com.arhs.spring.cache.mongo.serializer;

import java.io.IOException;

public interface Serializer {

	public byte[] serialize(Object obj) throws IOException;

	public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException;

}
