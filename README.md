# EasyDI - Dependency Injection for Java

[![Build Status](https://travis-ci.org/lestard/EasyDI.svg?branch=master)](https://travis-ci.org/lestard/EasyDI)

**EasyDI** is a small dependency injection (DI) library for java projects.

It's designed for small projects that don't need a full-blown DI-framework. 
To be as easy as possible EasyDI has less features compared to other DI frameworks and some limitations:

- Only constructor injection is supported, **no** setter/field injection
- No `PostConstruct` or `PreDestroy`
- Uses some JSR-330 annotations (`javax.inject.*`) but is **not** a compliant implementation of JSR-330

If you like to use dependency injection (which is always a good idea) but EasyDI doesn't fit your needs
 you might want to try other DI frameworks like [CDI](http://www.cdi-spec.org/), [Guice](https://github.com/google/guice) 
 or [Dagger](https://square.github.io/dagger/).


## How to Use

#### 1. Add the library to your project
 
At the moment EasyDI is in an early alpha version. It will be published to the maven central repository in the future
but at the moment it's only available in the Sonatype snapshot repository. 

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

#### 2. Create your classes

Write your Java classes. When your class needs an instance of another class, simply add this dependency as a constructor parameter.


```java
public class Car {

    private final Engine engine;

    public Car(Engine engine){
        this.engine = engine;
    }

    public void drive() {
        engine.start();
        engine.accelerate();
    }
}

public class Engine {
    public void start(){
    }

    public void accelerate(){
    }
}
```

#### 3. Use EasyDI to get instances of your classes

```java
import eu.lestard.easydi.EasyDI;

public class CarApp {

    public static void main(String...args){
        EasyDI easyDI = new EasyDI();

        final Car car = easyDI.getInstance(Car.class);

        car.drive();
    }
}
```


First create an instance of `EasyDi`, then use the method `getInstance` to get an instance of your class and use it.

For this simple use case EasyDI doesn't need any annotations or configuration. This is the case when every class in
the dependency graph defines exactly one public constructor or has no constructor at all 
(in this case the java compiler will automatically add an implicit no-args constructor). 
