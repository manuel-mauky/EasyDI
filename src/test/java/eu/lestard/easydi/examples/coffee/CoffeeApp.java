package eu.lestard.easydi.examples.coffee;

import eu.lestard.easydi.EasyDI;

public class CoffeeApp {

    public static void main(String...args){
        EasyDI easyDI = new EasyDI();
        easyDI.bindInterface(WaterSupply.class, WaterTank.class);

        easyDI.bindProvider(BeanContainer.class, () -> {
            final BeanContainer beanContainer = new BeanContainer();
            beanContainer.setAmount(100);
            return beanContainer;
        });

        CoffeeMachine coffeeMachine = easyDI.getInstance(CoffeeMachine.class);

        coffeeMachine.makeCoffee();
    }
}
