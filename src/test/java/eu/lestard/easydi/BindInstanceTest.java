package eu.lestard.easydi;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Bind Instance")
class BindInstanceTest {


    private static class Example implements MyInterface {
    }

    interface MyInterface {
    }

    static abstract class AbstractExample {
    }

    private EasyDI easyDI;

    @BeforeEach
    void setup() throws Exception {
        easyDI = new EasyDI();
    }


    @Test
    @DisplayName("works with normal class")
    void success_bindInstance() {

        Example example = new Example();

        easyDI.bindInstance(Example.class, example);

        final Example instance = easyDI.getInstance(Example.class);

        assertThat(instance).isSameAs(example);
    }

    @Test
    @DisplayName("works with interface")
    void success_interface() {
        Example example = new Example();

        easyDI.bindInstance(MyInterface.class, example);

        final MyInterface instance = easyDI.getInstance(MyInterface.class);

        assertThat(instance).isSameAs(example);
    }

    @Test
    @DisplayName("works with abstract class")
    void success_abstractClass() {

        AbstractExample example = new AbstractExample() {
        };

        easyDI.bindInstance(AbstractExample.class, example);

        final AbstractExample instance = easyDI.getInstance(AbstractExample.class);

        assertThat(instance).isSameAs(example);
    }
}
