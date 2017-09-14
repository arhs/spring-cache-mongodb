package com.arhs.spring.cache.mongo.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Adapts HessianSerializer so it can be used in Redis. User: msellers Date:
 * 9/3/14 Time: 10:17 AM
 */
public class HessianSerializer implements Serializer {

	private static final Logger m_logger = LoggerFactory.getLogger(HessianSerializer.class);

	@Override
	public byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Hessian2Output out = new Hessian2Output(bos);
			out.startMessage();
			out.writeObject(obj);
			out.completeMessage();
			out.close();
			byte[] array = bos.toByteArray();

			return array;
		} catch (Exception e) {
			m_logger.error("Error Serializing a value to be put into the Redis cache", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Object deserialize(byte[] bytes) {
		try {
			Object toReturn = null;

			if (bytes != null) {
				ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
				Hessian2Input in = new Hessian2Input(bin);
				in.startMessage();

				toReturn = in.readObject();
			}

			return toReturn;
		} catch (Exception e) {
			m_logger.error("Error De-Serializing a value from the Redis cache", e);
			throw new RuntimeException(e.getMessage());
		}
	}

}
