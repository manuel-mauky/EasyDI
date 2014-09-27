# EasyDI - Dependency Injection for Java

[![Build Status](https://travis-ci.org/lestard/EasyDI.svg?branch=master)](https://travis-ci.org/lestard/EasyDI)

**EasyDI** is a small dependency injection library for java projects.

It's designed for small projects that don't need a full-blown DI-framework. 
To be as easy as possible EasyDI has less features compared to other DI frameworks and some limitations:

- Only constructor injection is supported, **no** setter/field injection
- No `PostConstruct` or `PreDestroy` is supported
- Uses some JSR-330 annotations (`javax.inject.*`) but is **not** a compliant implementation of JSR-330

If you like to use a dependency injection (which is always a good idea) but EasyDI doesn't fit to your needs
 you might want to try other DI frameworks like [CDI](http://www.cdi-spec.org/), [Guice](https://github.com/google/guice) 
 or [Dagger](https://square.github.io/dagger/).
 
 
## How to Use

At the moment Easy-DI is in an early alpha state. Therefore only snapshot
releases are available. 

Gradle: 
```groovy
// add the sonatype snapshot repository
repositories {
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots/"
    }
}

dependencies {
    compile 'eu.lestard:easy-di:0.1.0-SNAPSHOT'
}
```

Maven:
```xml
<dependency>
    <groupId>eu.lestard</groupId>
    <artifactId>easy-di</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```