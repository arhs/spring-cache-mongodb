package com.arhs.spring.cache.mongo.serializer;

import java.io.IOException;

/**
 * Created by dan on 3/1/17.
 */
public interface Serializer {

	public byte[] serialize(Object obj) throws IOException;

	public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException;

}
