package eu.lestard.easydi;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Test for non-cyclic dependencies")
class NoCyclicDependenciesTest {

    public static class Root {
        public Root(DepOne depOne, DepThree depThree) {
        }
    }

    public static class DepOne {
        public DepOne(DepTwo depTwo, DepThree depThree) {
        }
    }

    public static class DepTwo {
        public DepTwo(DepThree depThree) {
        }
    }

    public static class DepThree {
        public DepThree() {
        }
    }


    private EasyDI easyDI;

    @BeforeEach
    void setup() throws Exception {
        easyDI = new EasyDI();
    }

    @Test
    void success_NoCyclicDependencies() {
        final Root instance = easyDI.getInstance(Root.class);
        assertThat(instance).isNotNull();
    }

}
