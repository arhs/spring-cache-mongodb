package com.arhs.spring.cache.mongo;

import com.arhs.spring.cache.mongo.serializer.JavaSerializer;
import org.junit.Assert;
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
	private static final JavaSerializer serializer = new JavaSerializer();

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void testBuildWithSerializer() {
		MongoCacheBuilder builder = new MongoCacheBuilder(
			COLLECTION_NAME,
			mongoTemplate,
			CACHE_NAME
		);

		builder.withSerializer(serializer);
		builder.withTTL(TTL);
		builder.withFlushOnBoot(false);

		MongoCache cache = builder.build();
		Assert.assertEquals(COLLECTION_NAME, cache.getCollectionName());
		Assert.assertEquals(mongoTemplate, cache.getMongoTemplate());
		Assert.assertEquals(CACHE_NAME, cache.getCacheName());
		Assert.assertEquals(serializer, cache.getSerializer());
		Assert.assertEquals(TTL, cache.getTtl());
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
		MongoCache cache = builder.build();

		Assert.assertEquals(COLLECTION_NAME, cache.getCollectionName());
		Assert.assertEquals(mongoTemplate, cache.getMongoTemplate());
		Assert.assertEquals(CACHE_NAME, cache.getCacheName());
		Assert.assertEquals(TTL, cache.getTtl());
	}

}
