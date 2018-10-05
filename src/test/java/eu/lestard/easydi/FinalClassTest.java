package eu.lestard.easydi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Final classes")
public class FinalClassTest {

    final class Dependency {
        public Dependency() {}
    }

    final class Example {
        Dependency dep;
        public Example(Dependency dep){
            this.dep = dep;
        }
    }

    private EasyDI easyDI;

    @BeforeEach
    void setup() throws Exception {
        easyDI = new EasyDI();
    }

    @Test
    @DisplayName("final class can be instantiated")
    void success_finalClassCanBeInstantiated() {
        final Example instance = easyDI.getInstance(Example.class);
        assertThat(instance).isNotNull();

        assertThat(instance.dep).isNotNull();
    }

}
