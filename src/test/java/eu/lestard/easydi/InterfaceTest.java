package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Singleton;

import static org.assertj.core.api.Assertions.*;

/**
 * This test is used to verify the behaviour of EasyDI dealing with interfaces.
 */
public class InterfaceTest {

    public static interface A {
    }

    public static interface B {
    }

    public static class ExampleOne implements A{
    }

    public static class ExampleTwo implements A, B {
    }


    private EasyDI easyDI;

    @Before
    public void setup() throws Exception{
        easyDI = new EasyDI();
    }


    @Test
    public void success_withBinding(){
        easyDI.bindInterface(A.class, ExampleOne.class);

        final A instance = easyDI.getInstance(A.class);
        assertThat(instance).isNotNull().isInstanceOf(ExampleOne.class);
    }


    @Test(expected = IllegalStateException.class)
    public void fail_noBinding(){
        easyDI.getInstance(A.class);
    }

    @Test
    public void success_lastBindingIsUsed(){
        easyDI.bindInterface(A.class, ExampleOne.class);
        easyDI.bindInterface(A.class, ExampleTwo.class);

        final A instance = easyDI.getInstance(A.class);
        assertThat(instance).isNotNull().isInstanceOf(ExampleTwo.class);
    }



    @Singleton
    public static interface WannabeSingleton {}

    public static class NonSingleton implements WannabeSingleton{}

    /**
     * The {@link javax.inject.Singleton} annotation is ignored when it is added to
     * interfaces.
     */
    @Test
    public void fail_interfacesCantBeMarkedAsSingleton(){

        easyDI.bindInterface(WannabeSingleton.class, NonSingleton.class);

        final WannabeSingleton instanceOne = easyDI.getInstance(WannabeSingleton.class);
        final WannabeSingleton instanceTwo = easyDI.getInstance(WannabeSingleton.class);

        assertThat(instanceOne).isNotSameAs(instanceTwo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_bindInterface_paramIsNotAnInterface(){
        easyDI.bindInterface(ExampleOne.class, ExampleOne.class);
    }


    public static interface SuperInterface {}

    public static interface SubInterface extends SuperInterface {}

    @Test(expected = IllegalArgumentException.class)
    public void fail_bindInterface_secondParamIsInterface(){
        easyDI.bindInterface(SuperInterface.class, SubInterface.class);
    }


    public static abstract class AbstractA implements A {}

    @Test(expected = IllegalArgumentException.class)
    public void fail_bindInterface_secondParamIsAbstractClass(){
        easyDI.bindInterface(A.class, AbstractA.class);
    }
}
