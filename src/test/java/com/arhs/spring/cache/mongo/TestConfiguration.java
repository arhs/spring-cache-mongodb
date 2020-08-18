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

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import cz.jirutka.spring.embedmongo.EmbeddedMongoBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;

/**
 * Spring Configuration for basic integration tests.
 *
 * @author ARHS Spikeseed
 */
@Configuration
public class TestConfiguration {

    private static final String DATABASE_NAME = "test";
    private static final String IP_ADDRESS = "127.0.0.1";

    /**
     * Gets a {@link MongoTemplate} instance.
     *
     * @return the {@link MongoTemplate} instance.
     * @throws IOException throws if the connection has failed.
     */
    @Bean
    public MongoTemplate mongoTemplate() throws IOException {
        final int port = allocateRandomPort();
        new EmbeddedMongoBuilder().bindIp(IP_ADDRESS).port(port).build();
        final MongoClient mongoClient = createMongoClientForPort(port);
        final SimpleMongoClientDbFactory simpleMongoDbFactory = new SimpleMongoClientDbFactory(mongoClient, DATABASE_NAME);

        return new MongoTemplate(simpleMongoDbFactory);
    }

    private static int allocateRandomPort() {
        try {
            final ServerSocket serverSocket = new ServerSocket(0);
            final int port = serverSocket.getLocalPort();
            serverSocket.close();

            return port;
        } catch (IOException e) {
            throw new RuntimeException("Failed to acquire a random free port", e);
        }
    }

    private static MongoClient createMongoClientForPort(int port) {
        return MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(Arrays.asList(
                                        new ServerAddress(IP_ADDRESS, port))))
                        .build());
    }
}
