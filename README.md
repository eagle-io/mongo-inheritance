# mongo-inheritance


![Build Status](https://eagle-io.semaphoreci.com/badges/mongo-inheritance.svg?style=shields)

Spring Data MongoDB includes a `_class` field with each document; this is used by the object mapper to instantiate the correct model. The default repository implementation however, does not constrain queries by `_class`, which can lead to confusing results.

This repository implementation ensures queries only consider documents matching or inheriting from the type defined by the repository declaration, e.g. `MongoRepository<Car, ObjectId>()` will only consider documents with a `_class` field that matches or extends `Car`.

## Usage

```xml
<dependency>
  <groupId>io.eagle</groupId>
  <artifactId>mongo-inheritance</artifactId>
  <version>1.0.10</version>
</dependency>
```

Include configuration to ensure all repositories are inheritance aware.

```java
@Configuration
@EnableMongoInheritanceRepositories(basePackages={"io.eagle.mongo.models"})
public class AppConfig() {}
```

Rules of operation:
- All entity classes for which a repository interface is to be declared have a `@Document` annotation.
- All entity classes that share a common superclass are stored in the same collection.
- The shared common superclasses are usually abstract classes. They don't have to be, but usually are.
- To support inheritance, all subclasses (concrete classes) use `@TypeAlias` annotation to specify a specific marker used to identify class type once stored in the database.

Functionality:
- Superclass repositories should work on all data within a collection.
- Subclass repositories should only work on data that are of the subclass type - meaning that a condition
 involving the `_class` field should be automatically added to all queries before they reach MongoDB.

Inspired by https://github.com/l0co/spring-data-mongodb-inheritance-test
