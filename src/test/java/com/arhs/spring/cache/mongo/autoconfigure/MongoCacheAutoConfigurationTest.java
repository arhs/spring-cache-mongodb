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
package com.arhs.spring.cache.mongo.autoconfigure;

import com.arhs.spring.cache.mongo.MongoCache;
import com.arhs.spring.cache.mongo.MongoCacheManager;
import com.arhs.spring.cache.mongo.TestConfiguration;
import com.arhs.spring.cache.mongo.UnitTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit tests for {@code MongoCacheAutoConfiguration} class.
 *
 * @author ARHS Spikeseed
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class MongoCacheAutoConfigurationTest extends UnitTestBase {

    private static final String CACHE_NAME = "cache";
    private static final String COLLECTION_NAME = "collection";
    private static final boolean FLUSH_ON_BOOT = false;
    private static final long TTL = 30;

    /**
     * Executes before the execution of tests.
     */
    public void load() {
        context = load(
                new Class<?>[] { MongoCacheAutoConfiguration.class },
                "spring.cache.mongo.caches[0].ttl:" + TTL,
                "spring.cache.mongo.caches[0].cacheName:" + CACHE_NAME,
                "spring.cache.mongo.caches[0].collectionName:" + COLLECTION_NAME,
                "spring.cache.mongo.caches[0].flushOnBoot:" + FLUSH_ON_BOOT
        );
    }

    /**
     * Test for using of {@code MongoCacheManager} instance.
     */
    @Test
    public void testInstance() {
        load();
        assertBeanExists(MongoCache.class);

        final MongoCacheManager manager = context.getBean(MongoCacheManager.class);
        Assert.assertNotNull(manager);
    }

    /**
     * Test for properties of {@code MongoCacheManager} instance.
     */
    @Test
    public void testProperties() {
        load();
        assertBeanExists(MongoCacheManager.class);

        final MongoCacheManager manager = context.getBean(MongoCacheManager.class);
        Assert.assertNotNull(manager);

        final MongoCache cache = (MongoCache) manager.getCache(CACHE_NAME);
        Assert.assertNotNull(cache);
        Assert.assertEquals(TTL, cache.getTtl());
        Assert.assertEquals(CACHE_NAME, cache.getName());
        Assert.assertEquals(COLLECTION_NAME, cache.getCollectionName());
        Assert.assertEquals(FLUSH_ON_BOOT, cache.isFlushOnBoot());
    }

}
