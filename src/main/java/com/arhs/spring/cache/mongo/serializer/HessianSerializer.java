package com.arhs.spring.cache.mongo.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements Serializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(HessianSerializer.class);

	@Override
	public byte[] serialize(Object obj) {
		Hessian2Output out = null;

		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
		) {
			out = new Hessian2Output(bos);

			out.startMessage();
			out.writeObject(obj);
			out.completeMessage();
			out.close();

			return bos.toByteArray();
		} catch (Exception e) {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
				}
			}
			LOGGER.error("Error Serializing a value to be put into the Redis cache", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Object deserialize(byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		try (
			ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
		) {
			Hessian2Input in = new Hessian2Input(bin);
			in.startMessage();

			return in.readObject();
		} catch (Exception e) {
			LOGGER.error("Error De-Serializing a value from the Redis cache", e);
			throw new RuntimeException(e.getMessage());
		}
	}

}
