package eu.lestard.easydi.examples.coffee;

public class BeanContainer {

    public BeanContainer(){
        System.out.println("new BeanContainer()");
    }

    public void getBeans(int amount){
        System.out.println("BeanContainer: here you have " + amount + " beans");
    }

    public void setAmount(int i) {

    }
}
