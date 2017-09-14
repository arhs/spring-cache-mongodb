package com.arhs.spring.cache.mongo.serializer;

import java.io.*;

public class JavaSerializer implements Serializer {
	@Override
	public byte[] serialize(Object obj) throws IOException {
		try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			 final ObjectOutputStream output = new ObjectOutputStream(buffer)) {

			output.writeObject(obj);

			return buffer.toByteArray();
		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		try (final ByteArrayInputStream buffer = new ByteArrayInputStream(bytes);
			 final ObjectInputStream output = new ObjectInputStream(buffer)) {
			return output.readObject();
		}
	}
}
