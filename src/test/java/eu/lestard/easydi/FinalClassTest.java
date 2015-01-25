package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FinalClassTest {

    private EasyDI easyDI;

    @Before
    public void setup() throws Exception{
        easyDI = new EasyDI();
    }

    @Test
    public void success_finalClassCantBeInjected(){

        final class Example {
            public Example(){
            }
        }


        final Example instance = easyDI.getInstance(Example.class);
        assertThat(instance).isNotNull();
    }

}
