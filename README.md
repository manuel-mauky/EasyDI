# EasyDI - Dependency Injection for Java

EasyDI is a small dependency injection library for java projects.

It's designed for small projects that don't need a full-blown DI-framework. 
To be as easy as possible EasyDI has less features compared to other DI frameworks and some limitations:

- Only constructor injection is supported, **no** setter/field injection
- No `PostConstruct` or `PreDestroy` is supported
- Uses some JSR-330 annotations (`javax.inject.*`) but is **not** a compliant implementation of JSR-330

If you like to use a dependency injection (which is always a good idea) but EasyDI doesn't fit to your needs
 you might want to try other DI frameworks like [CDI](http://www.cdi-spec.org/), [Guice](https://github.com/google/guice) 
 or [Dagger](https://square.github.io/dagger/).