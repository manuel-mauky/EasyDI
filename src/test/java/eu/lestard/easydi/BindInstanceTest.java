package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BindInstanceTest {


    public static class Example implements MyInterface{
    }

    public static interface MyInterface {
    }

    public static abstract class AbstractExample {
    }

    private EasyDI easyDI;

    @Before
    public void setup() throws Exception{
        easyDI = new EasyDI();
    }


    @Test
    public void success_bindInstance(){

        Example example = new Example();

        easyDI.bindInstance(Example.class, example);

        final Example instance = easyDI.getInstance(Example.class);

        assertThat(instance).isSameAs(example);
    }

    @Test
    public void success_interface(){
        Example example = new Example();

        easyDI.bindInstance(MyInterface.class, example);

        final MyInterface instance = easyDI.getInstance(MyInterface.class);

        assertThat(instance).isSameAs(example);
    }

    @Test
    public void success_abstractClass(){

        AbstractExample example = new AbstractExample() {
        };

        easyDI.bindInstance(AbstractExample.class, example);

        final AbstractExample instance = easyDI.getInstance(AbstractExample.class);

        assertThat(instance).isSameAs(example);
    }
}
