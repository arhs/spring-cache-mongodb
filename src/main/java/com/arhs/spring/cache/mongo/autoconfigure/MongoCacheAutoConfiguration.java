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

import com.arhs.spring.cache.mongo.MongoCacheBuilder;
import com.arhs.spring.cache.mongo.MongoCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto configuration} for {@code MongoCacheManager} support.
 *
 * @author ARHS Spikeseed
 */
@Configuration
@ConditionalOnClass(MongoTemplate.class)
@ConditionalOnMissingBean(CacheManager.class)
@EnableConfigurationProperties(MongoCachePropertiesList.class)
public class MongoCacheAutoConfiguration {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoCachePropertiesList properties;

    /**
     * Creates a instance of the {@code CacheManager} class.
     * Only create it if there is at least one cache defined.
     *
     * @return the instance of {@code CacheManager} class.
     */
    @Bean
    @ConditionalOnProperty("spring.cache.mongo.caches[0].cacheName")
    public CacheManager mongoCacheManager() {
        return new MongoCacheManager(mongoCacheBuilders());
    }

    private List<MongoCacheBuilder> mongoCacheBuilders() {

        List<MongoCacheBuilder> builders = new ArrayList<>();

        if (properties.getCaches() != null) {
            for (MongoCacheProperties mongoCacheProperties : properties.getCaches()) {
                builders.add(
                        MongoCacheBuilder
                                .newInstance(
                                        mongoCacheProperties.getCollectionName(),
                                        mongoTemplate,
                                        mongoCacheProperties.getCacheName()
                                )
                                .withTTL(mongoCacheProperties.getTtl())
                                .withFlushOnBoot(mongoCacheProperties.isFlushOnBoot())
                );
            }
        }

        return builders;
    }

}
