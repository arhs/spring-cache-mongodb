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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link MongoCacheManager}.
 *
 * @author ARHS Spikeseed
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class MongoCacheManagerTest {

    private static final String CACHE_NAME = "cache";
    private static final String COLLECTION_NAME = "test";

    @Autowired
    private MongoTemplate mongoTemplate;
    private MongoCacheManager manager;

    @Before
    public void setup() {
        MongoCacheBuilder defaultCacheBuilder = MongoCacheBuilder.newInstance(COLLECTION_NAME, mongoTemplate, CACHE_NAME);
        this.manager = new MongoCacheManager(Arrays.asList(defaultCacheBuilder));
        manager.afterPropertiesSet();
    }

    /**
     * Test for {@link MongoCacheManager#getCache(String)}
     */
    @Test
    public void cache() {
        final Cache cache = manager.getCache(CACHE_NAME);
        Assert.assertNotNull(cache);
        Assert.assertEquals(cache.getName(), CACHE_NAME);
        assertThat(cache, instanceOf(MongoCache.class));

        final MongoCache mongoCache = (MongoCache) cache;
        Assert.assertEquals(mongoCache.getNativeCache(), mongoTemplate);
    }

    /**
     * Test for {@link MongoCacheManager#getCache(String)}
     */
    @Test
    public void getCache() {
        final Cache cache = manager.getCache(CACHE_NAME);
        Assert.assertNotNull(cache);

        final Cache invalidCache = manager.getCache("invalid");
        Assert.assertNull(invalidCache);
    }

    /**
     * Test for {@link MongoCacheManager#getCacheNames()}
     */
    @Test
    public void getCacheNames() {
        Assert.assertNotNull(manager.getCacheNames());
        Assert.assertEquals(manager.getCacheNames().size(), 1);
        assertThat(manager.getCacheNames(), hasItem(CACHE_NAME));
    }

}
