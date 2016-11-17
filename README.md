# spring-cache-mongodb

[![MIT License][license-image]][license-url]
[![Build Status][travis-image]][travis-url]
[![Coverage Status][coveralls-image]][coveralls-url]
[![standard-readme compliant][standard-readme-image]][standard-readme-url]

> Spring Cache implementation based on MongoDB

## Install

### Maven dependency

```xml
<dependency>
  <groupId>com.arhs-group</groupId>
  <artifactId>spring-cache-mongodb</artifactId>
  <version>1.0.1</version>
</dependency>
```

### From source
To install this library, you need:

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org/)

```bash
git clone https://github.com/arhs/spring-cache-mongodb.git
cd spring-cache-mongodb
mvn clean install
```

## Usage

### Custom configuration

To customize the creation of a cache manager, create a Java Bean in a [configuration class](http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-configuration-classes.html):

```java
@Autowired
private MongoTemplate mongoTemplate;

@Bean
public CacheManager cacheManager() {
	// Create a "cacheName" cache that will use the collection "collectionName" with a TTL 7 days.
	MongoCacheBuilder cache = MongoCacheBuilder.newInstance("collectionName", mongoTemplate, "cacheName");
	cache.withTTL(604800);
	List<Cache> caches = new ArrayList<>();
	caches.add(cache);

	// Create a manager which will make available the cache created previously.
	return new MongoCacheManager(caches);
}
```

### Autoconfiguration

There is also an autoconfiguration class that will setup everything for you provided you have expressed the following properties.

#### .properties

```properties
# TTL (in seconds). This property is optional, the default value is: 7 days.
spring.cache.mongo.caches[0].ttl =

# MongoDB collection name.
spring.cache.mongo.caches[0].collectionName =

# Cache name for the @Cacheable annotation.
spring.cache.mongo.caches[0].cacheName =

# Value that indicates if the collection must be flushed when the application starts.
spring.cache.mongo.caches[0].flushOnBoot =
```

#### YAML

```yml
spring:
  cache:
    mongo:
      caches:
        -
      	  # TTL (in seconds).
          ttl: 
          # MongoDB collection name.
          collectionName:
          # Cache name for the @Cacheable annotation.
          cacheName:
          # Value that indicates if the collection must be flushed when the application starts.
          flushOnBoot:
```

### How to use cache?

After the cache has been configured, you can use the `Cacheable` [annotation](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html). In the following example, the cache "myCache" is used like this:

```java
@Cacheable(value = "myCache", key = "#id")
public Model getModel(String id) {
	// [...]
}
```

The `id` parameter is used as document identifier. Note that the cache key must be a of type `java.lang.String`.

The `Model` object will be stored in MongoDB collection for future use (as the TTL has not expired). Note that cache elements must be serializable (i.e. implement `java.io.Serializable`).

## License

> The MIT License (MIT)
>
> Copyright (c) 2016 ARHS Group
>
> Permission is hereby granted, free of charge, to any person obtaining a copy
> of this software and associated documentation files (the "Software"), to deal
> in the Software without restriction, including without limitation the rights
> to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
> copies of the Software, and to permit persons to whom the Software is
> furnishet to do so, subject to the following conditions:
>
> The above copyright notice and this permission notice shall be included in
> all copies or substantial portions of the Software.
>
> THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
> OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
> FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
> THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
> LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
> OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
> THE SOFTWARE.

[license-image]: http://img.shields.io/badge/license-MIT-blue.svg
[license-url]: LICENSE
[travis-image]: https://travis-ci.org/arhs/spring-cache-mongo.svg?branch=master
[travis-url]: https://travis-ci.org/arhs/spring-cache-mongo
[standard-readme-image]: https://img.shields.io/badge/standard--readme-OK-green.svg
[standard-readme-url]: https://github.com/RichardLitt/standard-readme
[coveralls-image]: https://coveralls.io/repos/github/arhs/spring-cache-mongo/badge.svg
[coveralls-url]: https://coveralls.io/github/arhs/spring-cache-mongo
