package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}
