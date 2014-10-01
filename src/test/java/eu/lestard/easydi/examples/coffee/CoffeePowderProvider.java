package eu.lestard.easydi.examples.coffee;

public class CoffeePowderProvider {

    private final Mill mill;
    private final BeanContainer container;

    public CoffeePowderProvider(Mill mill, BeanContainer container){
        System.out.println("new CoffeePowderProvider(...)");
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
