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

## Tutorial

In the wiki there is a tutorial where all features of EasyDI are described. 
In the example we are creating a coffee machine application:
[wiki](https://github.com/lestard/EasyDI/wiki/Tutorial). 

The code for this example is located in the test source directory: 
[/src/test/java/eu/lestard/easydi/examples/coffee/](https://github.com/lestard/EasyDI/tree/master/src/test/java/eu/lestard/easydi/examples/coffee)

## Real-World Examples:

- [SnakeFX](https://github.com/lestard/SnakeFX): A Snake clone written in JavaFX
- [Nonogram](https://github.com/lestard/nonogram): A nonogram puzzle game written in JavaFX

## How to Use

### 1. Add the library to your project
 
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

### 2. Create your classes

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
        ...
    }

    public void accelerate(){
        ...
    }
}
```

### 3. Use EasyDI to get instances of your classes

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

For this simple use case EasyDI doesn't need any annotations or configuration.

### Optional Features

#### Multiple Constructors

When a class has more then one public constructor, you need to tell *EasyDI* which one it should use. This is 
done with the annotation `javax.inject.Inject`:

```java
public class Car {
    ...
    @Inject
    public Car(Engine engine){
        // this constructor will be used
        ...
    }
    
    public Car(){
        ...
    }    
}
```

#### Interfaces and Implementing classes

EasyDI doesn't know which implementing class it should use when an interface type is requested as a dependency. 
You have to tell it with the `easyDI.bindInterface(interfaceType, implementingType)` method.

```java
public interface Engine {}

public class GasolineEngine implements Engine {}

public class ElectricMotor implements Engine {}


EasyDI easyDi = new EasyDI();

easyDI.bindInterface(Engine.class, ElectricMotor.class);
...

```

#### Singletons

By default EasyDI will create new instances every time a dependency is requested. If you like to have only one
instance of a specific class you have to tell it EasyDI. There are two ways of doing this:

##### @Singleton

The recommended way is to use the `javax.inject.Singleton` annotation on the class that should be a singleton:

```java
@Singleton
public class Car {
...
}
```

##### EasyDI.markAsSingleton()

You can mark a class as singleton with the method `markAsSingleton`. This is useful when you for some reason can't 
modify the source code of the class you want to be a singleton (i.e. when it is part of a third-party library).

```java
EasyDI easyDI = new EasyDI();

easyDI.markAsSingleton(ThirdParty.class);
...
``` 

#### Providers

If you like to inject instances of a class that doesn't meet the requirements of EasyDI you can add a `javax.inject.Provider`
for this class. There are many use cases where this can be useful:

- There is only a factory method to get instances of this class but no constructors
- There are no public constructor/more than one public constructors and (for some reason) you can't add the `@Inject` annotation
- The class is implemented with the classical Singleton design pattern.
- You need to make some configuration on the created instance before it can be used for injection.
- When you like to use abstract classes as dependency (see next section)

```java
EasyDI easyDI = new EasyDI();

easyDI.bindProvider(Engine.class, new Provider<Engine>() {
    @Override
    public Engine get(){
        Engine engine = new Engine();
        engine.configureThis();
        engine.configureThat();
        return engine;
    }
});
```

With Java 8 lambdas you would write this: 

```java
easyDI.bindProvider(Engine.class, ()-> {
    Engine engine = new Engine();
    engine.configureThis();
    engine.configureThat();
    return engine;
});
```


#### Abstract classes

If an instance of an abstract class is requested, EasyDI can't know out of
the box which implementing class it should use. 

This is the same situation as with interfaces. Unlike interfaces at the moment there is no
explicit way of defining a binding for abstract classes. The reason is that there are
far more possibilities for (miss-)configuration when it comes to (abstract) class bindings.

When you like to use abstract classes as dependencies you will have to create a provider for this class:


### Note on Circular Dependencies

When using constructor injection without a DI framework, it isn't possible to
create circular dependencies. Look at the following example:

```java
public class A {
    public A (B b){}
}

public class B {
    public B (C c){}
}
 
public class C {
    public C (A a){}
}
```

You can't instantiate any of these classes with `new` because you can't provide the needed 
constructor params (except you pass `null` as constructor param).

The same is true for EasyDI. If you try to get an instance of one of these classes you will 
get an `IllegalStageException`:

```java
EasyDI easyDI = new EasyDI();

easyDI.getInstance(A.class); // IllegalStateException
```

Creating circular dependencies is generally a bad idea because it leads to tight coupling.
While other DI frameworks can circumvent this by creating proxy classes, EasyDI won't! 
Instead you have to fix your dependency graph.

   
    
