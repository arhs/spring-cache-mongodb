package com.arhs.spring.cache.mongo;

import com.arhs.spring.cache.mongo.serializer.JavaSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class MongoCacheBuilderTest {

	private static final String CACHE_NAME = "cache";
	private static final String COLLECTION_NAME = "test";
	private static final long TTL = 0;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void testBuildWithSerializer() {
		MongoCacheBuilder builder = new MongoCacheBuilder(
			COLLECTION_NAME,
			mongoTemplate,
			CACHE_NAME
		);
		builder.withSerializer(new JavaSerializer());
		builder.withTTL(TTL);
		builder.withFlushOnBoot(false);
		builder.build();
	}

	@Test
	public void testBuildWithoutSerializer() {
		MongoCacheBuilder builder = new MongoCacheBuilder(
			COLLECTION_NAME,
			mongoTemplate,
			CACHE_NAME
		);
		builder.withTTL(TTL);
		builder.withFlushOnBoot(false);
		builder.build();
	}

}
