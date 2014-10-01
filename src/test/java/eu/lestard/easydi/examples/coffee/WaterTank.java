package eu.lestard.easydi.examples.coffee;

public class WaterTank implements WaterSupply{

    public WaterTank(){
        System.out.println("new WaterTank()");
    }

    @Override
    public void getWater(){
        System.out.println("WaterTank:providing water");
    }
}
