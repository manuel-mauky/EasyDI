This tutorial will show you the usage of Easy-DI. 

Let's assume this class diagram for a coffee machine application: 
*todo add class diagram*

In this class diagram we can see the dependencies of each class:
The `CoffeeMachine` has a reference to the `WaterTank` and the `CoffeePowderProvider`.
The `CoffeePowderProvider` has a reference to the `BeanContainer` and to the `Mill`.

## 1. Write your classes
Let's start writing our classes. We begin with the ones that have no dependencies to other classes:

```java
public class Mill {
    public void grind(){
        System.out.println("Mill: Let's start grinding");
    }
}

public class BeanContainer {
    public void getBeans(int amount){
        System.out.println("BeanContainer: here you have " + amount + " beans");
    }
}

public class WaterTank {
    public void getWater(){
        System.out.println("WaterTank:providing water");
    }
}
```
Now the rest. When a class needs an instance of another class we simply add a constructor parameter to the class:

```java
public class CoffeePowderProvider {

    private final Mill mill;
    private final BeanContainer container;

    public CoffeePowderProvider(Mill mill, BeanContainer container){
        this.mill = mill;
        this.container = container;
    }

    public void getPowder(){
        System.out.println("CoffeePowderProvider: Start making coffee powder.");
        container.getBeans(10);
        mill.grind();
        System.out.println("CoffeePowderProvider: Here you have your coffee powder");
    }
}

public class CoffeeMachine {

    private final CoffeePowderProvider coffeePowderProvider;
    private final WaterTank waterTank;

    public CoffeeMachine(CoffeePowderProvider coffeePowderProvider, WaterTank waterTank ){
        this.coffeePowderProvider = coffeePowderProvider;
        this.waterTank = waterTank;
    }

    public void makeCoffee(){
        System.out.println("CoffeeMachine: Start making coffee");
        waterTank.getWater();
        coffeePowderProvider.getPowder();
        System.out.println("CoffeeMachine: I have all ingredients. Let's go");
        System.out.println("CoffeeMachine: ...");
        System.out.println("CoffeeMachine: Coffee is finished");
    }
}
```
Now we have all our "business" classes finished. The only thing that is missing is a class with a `main` method to start our programm. In the main method we need to create all our instances and put them together: 

```java
public class CoffeeApp {
    public static void main(String...args){
        Mill mill = new Mill();
        WaterTank waterTank = new WaterTank();
        BeanContainer beanContainer = new BeanContainer();
        CoffeePowderProvider powderProvider = new CoffeePowderProvider(mill,beanContainer);
        CoffeeMachine coffeeMachine = new CoffeeMachine(powderProvider,waterTank);

        coffeeMachine.makeCoffee();
    }
}
```

When we run our example app we get this output:
```
CoffeeMachine: Start making coffee
WaterTank:providing water
CoffeePowderProvider: Start making coffee powder.
BeanContainer: here you have 10 beans
Mill: Let's start grinding
CoffeePowderProvider: Here you have your coffee powder
CoffeeMachine: I have all ingredients. Let's go
CoffeeMachine: ...
CoffeeMachine: Coffee is finished
```

And this is it: This is dependency injection. You don't even need a framework for dependency injection!
This is *Inversion of Control*. Instead of instantiating them by themselves, every class tells you what other classes it needs. 
As you can see there is no big magic in DI.

But wait! There are at least two problems with this code: 
First of all, when your application grows you will end with a big stack of constructor calls. And it's an annoying task
to call constructors. Can't this be automated? Why can't a computer do this? Yes this is what a DI-Framework is doing. 

But the second problem is even worse: Our `CoffeeApp` class has static references to *all* our classes. Every time 
you make a change to one of your classes (rename it, move it to another package) or the dependencies of one
of your classes change the `CoffeeApp` has to be changed too.

So let's se what Easy-DI can do for us.

## 2. Use Easy-DI

Introducing Easy-DI to our example is ...easy. Instead of calling constructors you only create an instance of `EasyDI`
and get an instance of the `CoffeeMachine` from it:
 
```java
public static void main(String...args){
    EasyDI easyDI = new EasyDI();
    CoffeeMachine coffeeMachine = easyDI.getInstance(CoffeeMachine.class);

    coffeeMachine.makeCoffee();
}
```

This is all we have to do. No annoying constructor calls and no static dependencies to all classes anymore. 

And unlike some other DI-frameworks we didn't have to do any changes to our business classes. There was no need
to add annotations or other configuration (at least for this simple example).


To get an idea what EasyDI is doing under the hood we could add a *sysout* (`System.out.println`) to all our constructors like this:

```java
public class Mill {
    public Mill(){
        System.out.println("new Mill()");
    }

    ...
}
```

If we do this with all our classes and run the example we get this output:

```
new Mill()
new BeanContainer()
new CoffeePowderProvider(...)
new WaterTank()
new CoffeeMachine(...)
CoffeeMachine: Start making coffee
WaterTank:providing water
CoffeePowderProvider: Start making coffee powder.
BeanContainer: here you have 10 beans
Mill: Let's start grinding
CoffeePowderProvider: Here you have your coffee powder
CoffeeMachine: I have all ingredients. Let's go
CoffeeMachine: ...
CoffeeMachine: Coffee is finished
```

As you can see, EasyDI is doing the same as we have done by hand before: It's starting to instantiate the
classes that don't have dependencies.

## 3. Singletons

Our coffee machine is working. Let's implement some new features like a milk frother. The milk frother
will use hot steam to froth the milk. Therefore it needs a reference to the `WaterTank`:

```java
public class MilkFrother {

    private final WaterTank waterTank;

    public MilkFrother(WaterTank waterTank){
        System.out.println("new MilkFrother(...)");
        this.waterTank = waterTank;
    }

    public void makeMilkFroth(){
        waterTank.getWater();
        // heat the water up to get steam
        System.out.println("MilkFrother: making milk froth");
    }
}
```

Our `CoffeeMachine` now looks like this:

```java
public class CoffeeMachine {

    private final CoffeePowderProvider coffeePowderProvider;
    private final WaterTank waterTank;
    private final MilkFrother frother;

    public CoffeeMachine(CoffeePowderProvider coffeePowderProvider, WaterTank waterTank, MilkFrother frother ){
        System.out.println("new CoffeeMachine(...)");
        this.coffeePowderProvider = coffeePowderProvider;
        this.waterTank = waterTank;
        this.frother = frother;
    }

    public void makeCoffee(){
        System.out.println("CoffeeMachine: Start making coffee");
        waterTank.getWater();
        coffeePowderProvider.getPowder();
        frother.makeMilkFroth();

        System.out.println("CoffeeMachine: I have all ingredients. Let's go");
        System.out.println("CoffeeMachine: ...");
        System.out.println("CoffeeMachine: Coffee is finished");
    }
}
```

Let's try this out:
```
new Mill()
new BeanContainer()
new CoffeePowderProvider(...)
new WaterTank()
new WaterTank()
new MilkFrother(...)
new CoffeeMachine(...)
CoffeeMachine: Start making coffee
...
MilkFrother: making milk froth
...
CoffeeMachine: Coffee is finished
```

But what is this? The constructor of the `WaterTank` is called two times? That's bad. The reason is
that by default EasyDI will always create a new instance when one is needed. We have now two classes
that depend on a water tank: The `CoffeeMachine` and the `MilkFrother`. 

To fix this we need to tell EasyDI that there should only be exactly one instance of the `WaterTank`. The 
water tank should be a **Singleton**. There are two ways of doing this:

#### 1. Add `@Singleton` annotation to `WaterTank`

```java
import javax.inject.Singleton;

@Singleton
public class WaterTank {
...
}
```

When EasyDI finds out that a class is annotated with `javax.inject.Singleton` it will only create one instance and reuse
it everywhere it is needed.

#### 2. Use the `EasyDI.markAsSingleton()` method.

You can mark a class as singleton with the method `EasyDI.markAsSingleton(<yourclass>.class);`:

```java
public static void main(String...args){
    EasyDI easyDI = new EasyDI();
    easyDI.markAsSingleton(WaterTank.class);

    CoffeeMachine coffeeMachine = easyDI.getInstance(CoffeeMachine.class);

    coffeeMachine.makeCoffee();
}
```

The result is the same in both cases:

```
new Mill()
new BeanContainer()
new CoffeePowderProvider(...)
new WaterTank()
new MilkFrother(...)
new CoffeeMachine(...)
CoffeeMachine: Start making coffee
...
MilkFrother: making milk froth
...
CoffeeMachine: Coffee is finished
```

#### When should I use @Singleton, when `markAsSingleton`?

In general I would recommend to use the `@Singleton` annotation. 
This annotation is part of the **JSR-330** specification. If you choose to switch to another 
DI framework in the future it's likely that it will support this annotation as well and you probably won't have to 
change anything to get your singleton working again. 

The `markAsSingleton` method can be useful if you can't add the annotation for some reason.
For example when the class is part of a third-party library or you don't have access to the source files.
 
 
## 4. Interfaces

Our coffee machine business grows and now we like to add another variant of the coffee machine to our catalogue.
The new variant won't have a simple water tank anymore. Instead it will have a direct water supply. 

To make our application flexible we add an interface `WaterSupply` with two implementations:
The old `WaterTank` and our new class `DirectWaterSupply`.

```java
public interface WaterSupply {
    void getWater();
}

@Singleton
public class WaterTank implements WaterSupply{
    ...
    @Override
    public void getWater(){
        System.out.println("WaterTank:providing water");
    }
}

@Singleton
public class DirectWaterSupply implements WaterSupply {
    ...
    @Override
    public void getWater() {
        System.out.println("DirectWaterSupply: get water from the water tap");
    }
}
```

Additionally we change the type of the constructor parameters in `MilkFrother` and `CoffeeMachine`:
```java
public class CoffeeMachine {
    public CoffeeMachine(CoffeePowderProvider coffeePowderProvider, WaterSupply waterSupply, MilkFrother frother ){
        ...
    }
    ...
}

public class MilkFrother {
    public MilkFrother(WaterSupply waterSupply){
        ...
    }
    ...
}
```

But when we run our application like this we will get this exception:

```
java.lang.IllegalStateException: 
EasyDI can't create an instance of the class [interface WaterSupply]. 
It is an interface and there was no implementation class mapping defined for this type. 
Please use the 'bindInterface' method of EasyDI to define what 
implementing class should be used for a given interface.
```

As the exception says, EasyDI can't know which of the implementing classes it should use when an `WaterSupply` is requested.

To define this use the method `EasyDI.bindInterface`:

```java
public static void main(String...args){
    EasyDI easyDI = new EasyDI();
    easyDI.bindInterface(WaterSupply.class, WaterTank.class);

    CoffeeMachine coffeeMachine = easyDI.getInstance(CoffeeMachine.class);

    coffeeMachine.makeCoffee();
}
```

Now we get the expected output again:
```
...
CoffeeMachine: Coffee is finished
```


## 5. Multiple Constructors

EasyDI is expecting to find exactly one public constructor when it has to create an instance of a class.
If for some reason your class has more then one constructor, EasyDI can't know which one to use.

Look at this example:

```java
public class BeanContainer {
    public BeanContainer(){
        System.out.println("new BeanContainer()");
    }
    
    public BeanContainer(int initialAmount){
        
    }
    ...
}
```

When you run the example you will get this exception:
```
java.lang.IllegalStateException: 
EasyDI can't create an instance of the class [class BeanContainer]. 
There are more than one public constructors so I don't know which to use. 
Fix this by either make only one constructor public 
or annotate exactly one constructor with the javax.inject.Inject annotation.
```
You have to provide this information with the annotation `javax.inject.Inject` like this:

```java
import javax.inject.Inject;

public class BeanContainer {
    @Inject
    public BeanContainer(){
        System.out.println("new BeanContainer()");
    }
    
    public BeanContainer(int initialAmount){
        
    }
    ...
}
```

If there are more then one public constructors EasyDI will use the one that is annotated with `@Inject`.
Of cause you may not annotate more then one constructor. 

## 6. Provider Methods

Sometimes the shown features aren't sufficient for your use case. For example when you like
to use a class from a third-party library that has multiple constructors and you can't add an annotation to one of them.

Or the only way to get an instance of the third-party class is to use a factory method.

For these use cases you can define a **Provider** for a given type:

```java
import javax.inject.Provider;

public static void main(String...args){
    EasyDI easyDI = new EasyDI();
    
    easyDI.bindProvider(BeanContainer.class, new Provider<BeanContainer>() {
            @Override
            public BeanContainer get() {
                final BeanContainer beanContainer = new BeanContainer();
                beanContainer.setAmount(100);
                return beanContainer;
            }
        });

    CoffeeMachine coffeeMachine = easyDI.getInstance(CoffeeMachine.class);

    coffeeMachine.makeCoffee();
}
```

With java 8 lambdas this can be written in a shorter way:

```java
easyDI.bindProvider(BeanContainer.class, () -> {
        final BeanContainer beanContainer = new BeanContainer();
        beanContainer.setAmount(100);
        return beanContainer;
    });
}
```

With this configuration EasyDI will always call the given provider instance when an instance of `BeanContainer`
is requested.

