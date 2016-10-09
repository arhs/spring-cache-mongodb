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

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.util.Assert;

import java.util.*;

/**
 * CacheManager implementation that lazily builds {@link MongoCache}
 * instances for each {@link #getCache} request.
 *
 * @author ARHS Spikeseed
 */
public class MongoCacheManager extends AbstractCacheManager {

    private final Collection<MongoCacheBuilder> initialCaches;

    /**
     * Constructor.
     */
    public MongoCacheManager() {
        this(Collections.EMPTY_LIST);
    }

    /**
     * Constructor.
     *
     * @param initialCaches the caches to make available on startup.
     */
    public MongoCacheManager(final Collection<MongoCacheBuilder> initialCaches) {
        Assert.notEmpty(initialCaches, "At least one cache builder must be specified.");
        this.initialCaches = new ArrayList<>(initialCaches);
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        final Collection<Cache> caches = new LinkedHashSet<>(initialCaches.size());
        for (final MongoCacheBuilder cacheBuilder : initialCaches) {
            final MongoCache cache = cacheBuilder.build();
            caches.add(cache);
        }

        return caches;
    }

}
