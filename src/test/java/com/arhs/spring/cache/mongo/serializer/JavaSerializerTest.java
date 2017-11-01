package com.arhs.spring.cache.mongo.serializer;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

public class JavaSerializerTest {

	public static class SerializableBean implements Serializable {
		Date date;
		String string;
		Integer integer;
	}

	@Test
	public void testSerializeDeserialize() throws IOException, ClassNotFoundException {
		JavaSerializer serializer = new JavaSerializer();

		Date date = new Date();
		String string = "foobar";
		Integer integer = 1234;

		SerializableBean in = new SerializableBean();
		in.date = date;
		in.string = string;
		in.integer = integer;

		byte[] bytes = serializer.serialize(in);

		SerializableBean out = (SerializableBean) serializer.deserialize(bytes);

		Assert.assertEquals(string, out.string);
		Assert.assertEquals(integer, out.integer);
		Assert.assertEquals(date, out.date);
	}


}
