package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractClassTest {

    public static abstract class AbstractExample{}

    public class Example extends AbstractExample {}


    private EasyDI easyDI;

    @Before
    public void setup() throws Exception{
        easyDI = new EasyDI();
    }


    @Test(expected = IllegalStateException.class)
    public void fail_abstractClass_withoutProvider(){
        easyDI.getInstance(AbstractExample.class);
    }

    @Test
    public void success_abstractClass_withProvider(){
        easyDI.bindProvider(AbstractExample.class, Example::new);

        final AbstractExample instance = easyDI.getInstance(AbstractExample.class);

        assertThat(instance).isNotNull().isInstanceOf(Example.class);
    }

}
