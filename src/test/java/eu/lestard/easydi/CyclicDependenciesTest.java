package eu.lestard.easydi;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Cyclic dependencies")
class CyclicDependenciesTest {

    public static class Root {
        public Root(DepOne depOne) {
        }
    }

    public static class DepOne {
        public DepOne(DepTwo depTwo) {
        }
    }

    public static class DepTwo {
        public DepTwo(Root root) {
        }
    }

    private EasyDI easyDI;

    @BeforeEach
    void setup() throws Exception {
        easyDI = new EasyDI();
    }

    @Test
    @DisplayName("cycle is found")
    void fail_cyclicDependencies() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            easyDI.getInstance(Root.class);
        });

        assertThat(exception).hasStackTraceContaining("cyclic dependency was detected");
    }
}
