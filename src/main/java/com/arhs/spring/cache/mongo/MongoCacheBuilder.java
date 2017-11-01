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

import com.arhs.spring.cache.mongo.serializer.Serializer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * A builder for {@link MongoCache} instance.
 *
 * @author ARHS Spikeseed
 */
public class MongoCacheBuilder {

    private static final long DEFAULT_TTL = TimeUnit.DAYS.toSeconds(7);

    private boolean flushOnBoot;
    private String cacheName;
    private String collectionName;
    private MongoTemplate mongoTemplate;
    private long ttl;
    private Serializer serializer;

    /**
     * Constructor.
     *
     * @param collectionName a collection name.
     * @param mongoTemplate  a {@link MongoTemplate} instance.
     * @param cacheName      a name of the cache.
     */
    protected MongoCacheBuilder(final String collectionName, final MongoTemplate mongoTemplate, final String cacheName) {
        Assert.hasText(collectionName, "'collectionName' must be not null and must contain at least one non-whitespace character.");
        Assert.notNull(mongoTemplate, "'mongoTemplate' must be not null.");
        Assert.hasText(cacheName, "'cacheName' must be not null and must contain at least one non-whitespace character.");

        this.cacheName = cacheName;
        this.collectionName = collectionName;
        this.mongoTemplate = mongoTemplate;
        this.ttl = DEFAULT_TTL;
    }

    /**
     * Create a new builder instance with the given collection name.
     *
     * @param collectionName a collection name.
     * @param mongoTemplate  a {@link MongoTemplate} instance.
     * @param cacheName      a name of the cache.
     * @return a new builder
     */
    public static MongoCacheBuilder newInstance(String collectionName, MongoTemplate mongoTemplate, String cacheName) {
        return new MongoCacheBuilder(collectionName, mongoTemplate, cacheName);
    }

    /**
     * Build a new {@link MongoCache} with the specified name.
     *
     * @return a {@link MongoCache} instance.
     */
    public MongoCache build() {
        return new MongoCache(cacheName, collectionName, mongoTemplate, ttl, flushOnBoot, serializer);
    }

    /**
     * Give a value that indicates if the collection must be always flush.
     *
     * @param flushOnBoot a value that indicates if the collection must be always flush.
     * @return this builder for chaining.
     */
    public MongoCacheBuilder withFlushOnBoot(boolean flushOnBoot) {
        this.flushOnBoot = flushOnBoot;
        return this;
    }

    /**
     * Give a TTL to the cache to be built.
     *
     * @param ttl a time-to-live (in seconds).
     * @return this builder for chaining.
     */
    public MongoCacheBuilder withTTL(long ttl) {
        this.ttl = ttl;
        return this;
    }

    public MongoCacheBuilder withSerializer(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

}
