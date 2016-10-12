# spring-cache-mongodb

[![MIT License][license-image]][license-url]
[![Build Status][travis-image]][travis-url]
[![Coverage Status][coveralls-image]][coveralls-url]
[![standard-readme compliant][standard-readme-image]][standard-readme-url]

> Spring Cache implementation based on MongoDB

## Install

To install this library, you have need:

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org/)

```bash
git clone https://github.com/arhs/spring-cache-mongo.git
cd spring-cache-mongo
mvn clean install
```

## Usage

### Java

The first way is to create a Java Bean in a [configuration class](http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-configuration-classes.html):

```java
@Autowired
private MongoTemplate mongoTemplate;

@Bean
public CacheManager cacheManager() {
	MongoCacheBuilder cache = MongoCacheBuilder.newInstance("collectionName", mongoTemplate, "cacheName");
	cache.withTTL(604800);
	List<Cache> caches = new ArrayList<>();
	caches.add(cache);

	return new MongoCacheManager(caches);
}
```

### Properties

The second way is to use properties to create one or several caches:

```
# TTL (in seconds).
spring.cache.mongo.caches[0].ttl =

# MongoDB collection name.
spring.cache.mongo.caches[0].collectionName =

# Cache name for the @Cacheable annotation.
spring.cache.mongo.caches[0].cacheName =

# Value that indicates if the collection must be flushed when the application starts.
spring.cache.mongo.caches[0].flushOnBoot = false
```

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
