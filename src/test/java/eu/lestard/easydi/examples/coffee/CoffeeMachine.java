package eu.lestard.easydi.examples.coffee;

public class CoffeeMachine {

    private final CoffeePowderProvider coffeePowderProvider;
    private final WaterSupply waterTank;
    private final MilkFrother frother;

    public CoffeeMachine(CoffeePowderProvider coffeePowderProvider, WaterSupply waterSupply, MilkFrother frother ){
        System.out.println("new CoffeeMachine(...)");
        this.coffeePowderProvider = coffeePowderProvider;
        this.waterTank = waterSupply;
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
