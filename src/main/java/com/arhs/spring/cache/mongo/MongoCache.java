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

import com.arhs.spring.cache.mongo.domain.CacheDocument;
import com.arhs.spring.cache.mongo.serializer.JavaSerializer;
import com.arhs.spring.cache.mongo.serializer.Serializer;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Spring {@link org.springframework.cache.Cache} adapter implementation
 * on top of a MongoDB.
 *
 * @author ARHS Spikeseed
 */
public class MongoCache implements Cache {

    private static final long DEFAULT_TTL = TimeUnit.DAYS.toSeconds(30);
    private static final String INDEX_KEY_NAME = "creationDate";
    private static final String INDEX_NAME = "_expire";
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoCache.class);

    private final boolean flushOnBoot;
    private final String collectionName;
    private final String cacheName;
    private final MongoTemplate mongoTemplate;
    private final long ttl;

    private final Object lock = new Object();
    private final Serializer serializer;

    /**
     * Constructor.
     *
     * @param cacheName      a cache name.
     * @param collectionName a collection name.
     * @param mongoTemplate  a {@link MongoTemplate} instance.
     */
    public MongoCache(String cacheName, String collectionName, MongoTemplate mongoTemplate) {
        this(cacheName, collectionName, mongoTemplate, DEFAULT_TTL);
    }

    /**
     * Constructor.
     *
     * @param cacheName      a cache name.
     * @param collectionName a collection name.
     * @param mongoTemplate  a {@link MongoTemplate} instance.
     * @param ttl            a time-to-live (in seconds).
     */
    public MongoCache(String cacheName, String collectionName, MongoTemplate mongoTemplate, long ttl) {
        this(cacheName, collectionName, mongoTemplate, ttl, false, new JavaSerializer());
    }

    /**
     * Constructor.
     *
     * @param cacheName	     a cache name.
     * @param collectionName a collection name.
     * @param mongoTemplate  a {@link MongoTemplate} instance.
     * @param ttl			 a time-to-live (in seconds).
     * @param serializer     a serializer.
     */
    public MongoCache(String cacheName, String collectionName, MongoTemplate mongoTemplate, long ttl, Serializer serializer) {
        this(cacheName, collectionName, mongoTemplate, ttl, false, serializer);
    }

    /**
     * Constructor.
     *
     * @param cacheName      a cache name.
     * @param collectionName a collection name.
     * @param mongoTemplate  a {@link MongoTemplate} instance.
     * @param ttl            a time-to-live (in seconds).
     * @param flushOnBoot    a value that indicates if the collection must be always flush.
     */
    public MongoCache(String cacheName, String collectionName, MongoTemplate mongoTemplate, long ttl, boolean flushOnBoot) {
        this(cacheName, collectionName, mongoTemplate, ttl, flushOnBoot, new JavaSerializer());
    }

    /**
     * Constructor.
     *
     * @param cacheName      a cache name.
     * @param collectionName a collection name.
     * @param mongoTemplate  a {@link MongoTemplate} instance.
     * @param ttl            a time-to-live (in seconds).
     * @param flushOnBoot    a value that indicates if the collection must be always flush.
     * @param serializer     a serializer.
     */
    public MongoCache(String cacheName, String collectionName, MongoTemplate mongoTemplate, long ttl, boolean flushOnBoot, Serializer serializer) {
        Assert.hasText(cacheName, "'cacheName' must be not null and not empty.");
        Assert.notNull(collectionName, "'collectionName' must be not null.");
        Assert.notNull(collectionName, "'mongoTemplate' must be not null.");

        this.flushOnBoot = flushOnBoot;
        this.collectionName = collectionName;
        this.mongoTemplate = mongoTemplate;
        this.cacheName = cacheName;
        this.ttl = ttl;
        this.serializer = serializer == null ? new JavaSerializer() : serializer;

        initialize();
    }

    private void creationCollection() {
        mongoTemplate.getCollection(collectionName);
    }

    @Override
    public void clear() {
        mongoTemplate.remove(new Query(), CacheDocument.class, collectionName);
    }

    @Override
    public void evict(Object key) {
        Assert.isTrue(key instanceof String, "'key' must be an instance of 'java.lang.String'.");

        final String id = (String) key;
        final CriteriaDefinition criteria = Criteria.where("_id").is(id);
        final Query query = Query.query(criteria);

        mongoTemplate.remove(query, collectionName);
    }

    @Override
    public ValueWrapper get(Object key) {
        final Object value = getFromCache(key);
        if (value != null) {
            return new SimpleValueWrapper(value);
        }

        return null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        try {
            final Object value = getFromCache(key);
            if (value == null) {
                return null;
            }
            return type.cast(value);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Unable to cast the object.", e);
        }
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Assert.isTrue(key instanceof String, "'key' must be an instance of 'java.lang.String'.");
        Assert.notNull(valueLoader, "'valueLoader' must not be null");

        Object cached = getFromCache(key);
        if (cached != null) {
            return (T) cached;
        }

        synchronized (lock) {
            cached = getFromCache(key);
            if (cached != null) {
                return (T) cached;
            }

            T value;
            try {
                value = valueLoader.call();
            } catch (Throwable ex) {
                throw new ValueRetrievalException(key, valueLoader, ex);
            }

            ValueWrapper newCachedValue = putIfAbsent(key, value);
            if (newCachedValue != null) {
                return (T) newCachedValue.get();
            } else {
                return value;
            }
        }

    }

    /**
     * Gets whether the cache should delete all elements on boot.
     *
     * @return returns whether the cache should delete all elements on boot.
     */
    public final boolean isFlushOnBoot() {
        return flushOnBoot;
    }

    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public String getName() {
        return cacheName;
    }

    @Override
    public Object getNativeCache() {
        return mongoTemplate;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public String getCacheName() {
        return cacheName;
    }

    /**
     * Returns the TTL value for this cache.
     *
     * @return the ttl value.
     */
    public final long getTtl() {
        return ttl;
    }

    @Override
    public void put(Object key, Object value) {
        Assert.isTrue(key instanceof String, "'key' must be an instance of 'java.lang.String'.");

        try {
            final String id = (String) key;
            String result = null;
            if (value != null) {
                Assert.isTrue(value instanceof Serializable, "'value' must be serializable.");
                result = serialize(value);
            }

            final CacheDocument cache = new CacheDocument(id, result);
            mongoTemplate.save(cache, collectionName);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Can not serialize the value: %s", key), e);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Assert.isTrue(key instanceof String, "'key' must be an instance of 'java.lang.String'.");

        try {
            final String id = (String) key;
            String result = null;

            if (value != null) {
                Assert.isTrue(value instanceof Serializable, "'value' must be serializable.");
                result = serialize(value);
            }

            final CacheDocument cache = new CacheDocument(id, result);
            mongoTemplate.insert(cache, collectionName);
            return null;
        } catch (DuplicateKeyException e) {
            LOGGER.info(String.format("Key: %s already exists in the cache. Element will not be replaced.", key), e);
            return get(key);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot serialize the value for key: %s", key), e);
        }
    }

    private Object deserialize(String value) throws IOException, ClassNotFoundException {
        final Base64.Decoder decoder = Base64.getDecoder();
        final byte[] data = decoder.decode(value);
        return serializer.deserialize(data);
    }

    private Object getFromCache(Object key) {
        Assert.isTrue(key instanceof String, "'key' must be an instance of 'java.lang.String'.");

        final String id = (String) key;
        final CacheDocument cache = mongoTemplate.findById(id, CacheDocument.class, collectionName);

        if (cache != null) {
            final String element = cache.getElement();
            if (element != null && !"".equals(element)) {
                try {
                    return deserialize(element);
                } catch (IOException | ClassNotFoundException e) {
                    throw new IllegalStateException("Unable to read the object from cache.", e);
                }
            }
        }

        return null;
    }

    private void initialize() {
        creationCollection();

        if (isFlushOnBoot()) {
            clear();
        }

        final Index expireIndex = createExpireIndex();
        updateExpireIndex(expireIndex);
    }

    private String serialize(Object value) throws IOException {
        final Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(serializer.serialize(value));
    }

    private Index createExpireIndex() {
        final Index index = new Index();
        index.named(INDEX_NAME);
        index.on(INDEX_KEY_NAME, Sort.Direction.ASC);
        index.expire(ttl);

        return index;
    }

    private void updateExpireIndex(Index newExpireIndex) {
        final IndexOperations indexOperations = mongoTemplate.indexOps(collectionName);
        final DBCollection collection = mongoTemplate.getCollection(collectionName);
        final List<DBObject> indexes = collection.getIndexInfo();

        final Optional<DBObject> expireOptional = indexes.stream()
                .filter(index -> INDEX_NAME.equals(index.get("name")))
                .findFirst();

        if (expireOptional.isPresent()) {
            final DBObject expire = expireOptional.get();
            final long ttl = (long) expire.get("expireAfterSeconds");

            if (ttl != this.ttl) {
                indexOperations.dropIndex(INDEX_NAME);
            }
        }

        indexOperations.ensureIndex(newExpireIndex);
    }

}
