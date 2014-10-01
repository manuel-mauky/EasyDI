package eu.lestard.easydi.examples.coffee;

public class MilkFrother {

    private final WaterSupply waterTank;

    public MilkFrother(WaterSupply waterSupply){
        System.out.println("new MilkFrother(...)");
        this.waterTank = waterSupply;
    }

    public void makeMilkFroth(){
        waterTank.getWater();
        // heat the water up to get steam
        System.out.println("MilkFrother: making milk froth");
    }
}
