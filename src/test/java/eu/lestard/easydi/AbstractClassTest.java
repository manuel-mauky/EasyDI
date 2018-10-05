package eu.lestard.easydi;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Abstract Class") class AbstractClassTest {

    static abstract class AbstractExample {
    }

    private class Example extends AbstractExample {
    }


    private EasyDI easyDI;

    @BeforeEach
    void setup() throws Exception {
        easyDI = new EasyDI();
    }


    @Test
    @DisplayName("fails without a Provider")
    void fail_abstractClass_withoutProvider() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            easyDI.getInstance(AbstractExample.class);
        });

        assertThat(exception).hasStackTraceContaining("abstract class")
            .hasStackTraceContaining("no provider");
    }

    @Test
    @DisplayName("success with a Provider ")
    void success_abstractClass_withProvider() {
        easyDI.bindProvider(AbstractExample.class, Example::new);

        final AbstractExample instance = easyDI.getInstance(AbstractExample.class);

        assertThat(instance).isNotNull().isInstanceOf(Example.class);
    }

}
