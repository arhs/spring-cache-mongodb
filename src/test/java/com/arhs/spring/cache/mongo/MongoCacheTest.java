/**
 * MIT License
 *
 * Copyright (c) 2016 ARHS Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.arhs.spring.cache.mongo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit tests for {@link MongoCache}.
 *
 * @author ARHS Spikeseed
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class MongoCacheTest {

    private static final String CACHE_NAME = "cache";
    private static final String COLLECTION_NAME = "test";
    private static final long TTL = 0;

    @Autowired
    private MongoTemplate mongoTemplate;
    private MongoCache cache;

    @Before
    public void setup() {
        mongoTemplate.remove(new Query(), COLLECTION_NAME);
        cache = new MongoCache(CACHE_NAME, COLLECTION_NAME, mongoTemplate, TTL);
    }

    /**
     * Test for {@link MongoCache#MongoCache(String, String, MongoTemplate)}.
     */
    @Test
    public void constructor() {
        final String cacheName = cache.getName();
        Assert.assertEquals(CACHE_NAME, cacheName);

        final Object nativeCache = cache.getNativeCache();
        Assert.assertEquals(mongoTemplate, nativeCache);

        final MongoCache mongoCache = new MongoCache(CACHE_NAME, COLLECTION_NAME, mongoTemplate);
        Assert.assertNotEquals(0, mongoCache.getTtl());
    }

    /**
     * Test for {@link MongoCache#clear()}.
     */
    @Test
    public void clear() {
        cache.put("key1", "value");
        cache.put("key2", "value");
        long count = mongoTemplate.count(new Query(), COLLECTION_NAME);
        Assert.assertEquals(2, count);

        cache.clear();
        count = mongoTemplate.count(new Query(), COLLECTION_NAME);
        Assert.assertEquals(0, count);
    }

    /**
     * Test for {@link MongoCache#get(Object)}.
     */
    @Test
    public void get() {
        final String key = "key";
        final String value = "value";
        cache.put(key, value);

        final Cache.ValueWrapper wrapper = cache.get(key);
        Assert.assertNotNull(wrapper);
        Assert.assertNotNull(value, wrapper.get());
    }

    /**
     * Test for {@link MongoCache#getCollectionName()}.
     */
    @Test
    public void getCollectionName() {
        final String collectionName = cache.getCollectionName();
        Assert.assertEquals(COLLECTION_NAME, collectionName);
    }

    /**
     * Test for {@link MongoCache#getName()}.
     */
    @Test
    public void getName() {
        final String name = cache.getName();
        Assert.assertEquals(CACHE_NAME, name);
    }

    /**
     * Test for {@link MongoCache#getNativeCache()}.
     */
    @Test
    public void getNativeCache() {
        final Object nativeCache = cache.getNativeCache();
        Assert.assertEquals(mongoTemplate, nativeCache);
    }

    /**
     * Test for {@link MongoCache#getTtl()}.
     */
    @Test
    public void getTtl() {
        final long ttl = cache.getTtl();
        Assert.assertEquals(TTL, ttl);
    }

    /**
     * Test for {@link MongoCache#get{T}(Object)}.
     */
    @Test(expected = IllegalStateException.class)
    public void getWithCast() {
        final String key = "key";
        final String value = "value";
        cache.put(key, value);

        String valueInCache = cache.get(key, String.class);
        Assert.assertEquals(value, valueInCache);

        valueInCache = cache.get("key1", String.class);
        Assert.assertNull(valueInCache);

        cache.get(key, Double.class);
    }

    /**
     * Test for {@link MongoCache#evict(Object)}.
     */
    @Test
    public void evict() {
        final String key = "evictKey";
        cache.put(key, "value");
        cache.evict(key);

        final Cache.ValueWrapper wrapper = cache.get(key);
        Assert.assertNull(wrapper);
    }

    /**
     * Test for {@link MongoCache#put(Object, Object)}.
     */
    @Test
    public void put() {
        final String key = "key";
        final String value = "value";
        cache.put(key, value);

        final Cache.ValueWrapper wrapper = cache.get(key);
        Assert.assertNotNull(wrapper);
        Assert.assertEquals(value, wrapper.get());
    }

    /**
     * Test for {@link MongoCache#put(Object, Object)}.
     * Use {@link MongoCache#evict(Object)} if the value is null.
     */
    @Test
    public void putAndEvict() {
        final String key = "key1";
        cache.put(key, "value");
        Cache.ValueWrapper wrapper = cache.get(key);
        Assert.assertNotNull(wrapper);

        cache.put(key, null);
        wrapper = cache.get(key);
        Assert.assertNull(wrapper);
    }

    /**
     * Test for {@link MongoCache#putIfAbsent(Object, Object)}.
     */
    @Test
    public void putIfAbsent() {
        final String key = "key";
        final String value = "value";
        Cache.ValueWrapper wrapper = cache.putIfAbsent(key, value);
        Assert.assertNull(wrapper);

        wrapper = cache.putIfAbsent(key, value);
        Assert.assertNotNull(wrapper);
        Assert.assertEquals(value, wrapper.get());
    }
}
