# EasyDI - Dependency Injection for Java

[![Build Status](https://travis-ci.org/lestard/EasyDI.svg?branch=master)](https://travis-ci.org/lestard/EasyDI)

**EasyDI** is a small dependency injection (DI) library for java projects.

It's designed for small projects that don't need a full-blown DI-framework.
To be as easy as possible EasyDI has less features compared to other DI frameworks and some limitations:

- Only constructor injection is supported, **no** setter/field injection
- No `PostConstruct` or `PreDestroy`
- Uses some JSR-330 annotations (`javax.inject.*`) but is **not** a compliant implementation of JSR-330
- Java 8 only

If you like to use dependency injection (which is always a good idea) but EasyDI doesn't fit your needs
 you might want to try other DI frameworks like [CDI](http://www.cdi-spec.org/), [Guice](https://github.com/google/guice)
 or [Dagger](https://square.github.io/dagger/).

## Links

[JavaDoc 0.3.0](https://lestard.github.io/EasyDI/docs/0.3.0/eu/lestard/easydi/EasyDI.html)


##<a name="dependencies"></a> Maven Dependencies

**EasyDI** releases are available in the Maven Central Repository.
You can use it like this:

### Stable release

#### Gradle
```groovy
repositories {
    mavenCentral()
}

dependencies {
    compile 'eu.lestard:easy-di:0.3.0'
}
```

#### Maven

```xml
<dependency>
    <groupId>eu.lestard</groupId>
    <artifactId>easy-di</artifactId>
    <version>0.3.0</version>
</dependency>
```

### Current Development version (Snapshot)

The development version is published automatically to the Sonatype Snapshot repository.


#### Gradle
```groovy
repositories {
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots/"
    }
}

dependencies {
    compile 'eu.lestard:easy-di:0.4.0-SNAPSHOT'
}
```

#### Maven

```xml
<dependency>
    <groupId>eu.lestard</groupId>
    <artifactId>easy-di</artifactId>
    <version>0.4.0-SNAPSHOT</version>
</dependency>
```



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

There are two ways of using the library in your project:

1. Use a Build-System like Gradle or Maven. **EasyDI** is available in the [Maven Central Repository](https://search.maven.org/#search|ga|1|easy-di). See [Maven dependencies](#dependencies)
2. Download the ZIP file from the [github release page](https://github.com/lestard/EasyDI/releases/download/v0.3.0/easy-di-0.3.0.zip). The file contains the library JAR file and the JAR for [javax.inject](http://search.maven.org/#artifactdetails|javax.inject|javax.inject|1|jar) which is needed as dependency. Add both JAR files to the classpath of your project.


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

## Optional Features

### Multiple Constructors

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

### Interfaces and Implementing classes

EasyDI doesn't know which implementing class it should use when an interface type is requested as a dependency.
You have to tell it with the `easyDI.bindInterface(interfaceType, implementingType)` method.

```java
public interface Engine {}

public class GasolineEngine implements Engine {}

public class ElectricMotor implements Engine {}


EasyDI easyDi = new EasyDI();
easyDI.bindInterface(Engine.class, ElectricMotor.class);
...

final Engine engine = easyDI.getInstance(Engine.class);

assertThat(engine).isInstanceOf(ElectricMotor.class);
```

### Singletons

By default EasyDI will create new instances every time a dependency is requested. If there should only be a single instance of a specific class you have to tell EasyDI. There are two ways of doing this:

#### 1. @Singleton

The recommended way is to use the `javax.inject.Singleton` annotation on the class that should be a singleton:

```java
@Singleton
public class Car {
...
}
```

#### 2. EasyDI.markAsSingleton()

You can mark a class as singleton with the method `markAsSingleton`. This is useful when you for some reason can't
modify the source code of the class (i.e. when it is part of a third-party library).

```java
EasyDI easyDI = new EasyDI();

easyDI.markAsSingleton(ThirdParty.class);
...
```

### Providers

If you like to inject instances of a class that doesn't meet the requirements of EasyDI you can add a `javax.inject.Provider`
for this class. There are many use cases where this can be useful:

- There is only a factory method to get instances of this class but no constructors
- There is no public constructor or there are more than one public constructors and (for some reason) you can't add the `@Inject` annotation
- The class is implemented with the [classical Singleton design pattern](https://en.wikipedia.org/wiki/Singleton_pattern#Example).
- You need to make some configuration on the created instance before it can be used for injection.
- You like to use abstract classes as dependency (see next section)

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


### Abstract classes

If an instance of an abstract class is requested, EasyDI can't know out of
the box which implementing class it should use.

This is the same situation as with interfaces. Unlike interfaces at the moment there is no
explicit way of defining a binding for abstract classes. The reason is that there are
far more possibilities for (miss-)configuration when it comes to (abstract) class bindings.

When you like to use abstract classes as dependencies you will have to [create a provider](#providers) for this class.


### Lazy injection / lazy instantiation

In some use cases you need an instance of a class but at the time your constructor is called
this dependency isn't available yet or it shouldn't be instantiated at this time.
Instead you like to retrieve the instance at some later point in time.

Another similar use case is when you need a dependency only under some conditions and the construction of the dependency is expensive.

In both cases **lazy injection** is your friend. EasyDI can do lazy injection like this:

1. change the type of the constructor parameter from `T` to `javax.inject.Provider<T>`
2. call the method `get` on the provider instance when you need the actual instance

Example:

```java

public class Car {

    private Provider<Engine> engineProvider;

    public Car(Provider<Engine> engineProvider){
        this.engineProvider = engineProvider;
    }

    public void buildCar(){
        Engine engine =  engineProvider.get();

        ...
    }
```

In this example the instance of the `Engine` is only created when the `buildCar` method is called. In this
case the normal dependency injection mechanism of EasyDI with all configuration rules described above
will run and retrieve an instance of `Engine`.

**Recommendation**:
In general lazy injection should only be the last choice when you really can't inject an instance directly in the constructor.
Code with lazy injection will typically be harder to reason about. It's not trivial anymore to tell at which
point in time an instance of your class will be created.



### Bind instances

In some use cases you like to define that one specific instance is injected every time the given
type is requested. This is like a singleton configuration only that you define the exact instance on your own instead
of only defining that the given type is a singleton and let EasyDI create the instance.

```java
Engine engine = new Engine();
easyDI.bindInstance(Engine.class, engine);
```

This is a shortcut for this:

```java
Engine engine = new Engine();
easyDI.bindProvider(Engine.class, () -> engine);
```

The `bindInstance` method can also be used to configure instances for interfaces or abstract classes.



### Inject EasyDI context

In some use cases you like to have access to the EasyDI instance in one of your
classes to be able to get other instances at runtime.

To achieve this use this config:

```java
EasyDI context = new EasyDI();
context.bindProvider(EasyDI.class, ()-> context);
```

or

```java
EasyDI context = new EasyDI();
context.bindInstance(EasyDI.class, context);
```



Then you can inject `EasyDI` in you classes:


```java
Example.java:

public class Example {

    private EasyDI context;

    public Example(EasyDI context){
        this.context = context;
    }

    public void doSomething(){
        Other other = context.getInstance(Other.class);
        ...
    }
}

```


**Be aware that there are some drawbacks/characteristics with this approach you should keep in mind:**

- It's harder to reason about your code because the instantiation of classes may be delayed.
- It may be harder to reason about your EasyDI configuration as it's now possible make configurations in other classes besides the main class.
- You have a Dependency (`import`) to the EasyDI library in your business code.
This makes it harder to change the dependency library afterwards.
- If you forget the `bindProvider` configuration you will get a new context instance injected every time because the `EasyDI` class isn't marked as singleton. This has several consequences:
  - The new instance of EasyDI has no configuration. It's a totally different instance as the root context. NO configuration will be inherited from the root context.
  - The scope of the singleton configuration is limited to a single context. When there are two contexts it's possible that there are two instances of a class that was marked as singleton in your application!


**Therefore it's generally not recommended to inject `EasyDÌ` in your classes**. It should only be the last choice when there
is no other option for your use case.



If you still need the possibility to get instances from the dependency injection container in your business code you should probably use this approach:

```java
public interface InstanceProvider {
    <T> T getInstance(Class<T> type);
}



// in your main class

EasyDI context = new EasyDI();
context.bindProvider(InstanceProvider.class, () -> context::getInstance);



// in your business code
public class Example {

    private InstanceProvider context;

    public Example(InstanceProvider context){
        this.context = context;
    }

    public void doSomething(){
        Other other = context.getInstance(Other.class);
        ...
    }
}
```

This approach has some advantages over the previous one:
- no dependency to the EasyDI library in your business code anymore. This way switching to another DI library in the future should be easier.
- It's not possible to (accidentally) re-configure the EasyDI context outside of your main class.
- No way to mess up the singleton scope anymore.
If you forget the `bindProvider` configuration in the example you will now get an expressive error message that there is no provider for the interface `InstanceProvider` found.





---

## Note on Circular Dependencies

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
Instead you have to [fix your dependency graph](http://misko.hevery.com/2008/08/01/circular-dependency-in-constructors-and-dependency-injection/).


