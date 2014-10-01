package eu.lestard.easydi.examples.coffee;

public class DirectWaterSupply implements WaterSupply {

    public DirectWaterSupply(){
        System.out.println("new DirectWaterSupply()");
    }

    @Override
    public void getWater() {
        System.out.println("DirectWaterSupply: get water from the water tap");
    }
}
