package eu.lestard.easydi;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Injection of Context")
public class InjectContextTest {

    private EasyDI easyDI;

    @BeforeEach
    void setup() {
        easyDI = new EasyDI();
    }

    @Test
    @DisplayName("injection of context works")
    void success_injectContext() {
        class Dep {
            public Dep() {
            }
        }

        class Example {
            private EasyDI context;
            private Dep dep;

            public Example(EasyDI context) {
                this.context = context;
                this.dep = context.getInstance(Dep.class);
            }
        }


        easyDI.bindProvider(EasyDI.class, () -> easyDI);


        final Example instance = easyDI.getInstance(Example.class);

        assertThat(instance).isNotNull();

        assertThat(instance.context).isNotNull();
        assertThat(instance.dep).isNotNull();
    }

    @Test
    @DisplayName("context is a singleton")
    void success_context_is_a_singleton() {

        easyDI.bindProvider(EasyDI.class, () -> easyDI);

        final EasyDI context1 = easyDI.getInstance(EasyDI.class);
        final EasyDI context2 = easyDI.getInstance(EasyDI.class);

        assertThat(context1).isSameAs(context2);
    }

    @Test
    @DisplayName("endless recursion due to context doesn't work")
    void fail_endless_recursive_injection() {

        class Example {
            public Example(EasyDI context) {
                context.getInstance(Example.class);
            }
        }

        easyDI.bindProvider(EasyDI.class, () -> easyDI);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            easyDI.getInstance(Example.class);
        });

        assertThat(exception).hasStackTraceContaining("cyclic dependency was detected");
    }
}
