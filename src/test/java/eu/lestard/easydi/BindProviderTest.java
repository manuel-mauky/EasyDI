package eu.lestard.easydi;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Bind Provider")
class BindProviderTest {

    public static class ThirdParty {
        private ThirdParty() {
        }

        static ThirdParty thirdPartyFactory() {
            return new ThirdParty();
        }
    }

    interface MyInterface {
    }

    @Singleton
    private static class MySingleton {
    }

    public static class Example {
        final ThirdParty dep;

        public Example(ThirdParty dep) {
            this.dep = dep;
        }
    }


    private EasyDI easyDI;

    @BeforeEach
    void setup() throws Exception {
        easyDI = new EasyDI();
    }

    @Test
    @DisplayName("works with a factory method")
    void success_providerIsAvailable() {
        easyDI.bindProvider(ThirdParty.class, ThirdParty::thirdPartyFactory);

        final Example instance = easyDI.getInstance(Example.class);
        assertThat(instance).isNotNull();
        assertThat(instance.dep).isNotNull();
    }

    @Test
    @DisplayName("works with a anonymous inner class for an interface")
    void success_providerForInterface() {
        // the provider creates an anonymous inner class for the interface
        easyDI.bindProvider(MyInterface.class, () -> new MyInterface() {
        });

        // now it is working
        final MyInterface instance = easyDI.getInstance(MyInterface.class);
        assertThat(instance).isNotNull();
    }

    @Test
    @DisplayName("works in combination with Singleton")
    void success_providerAndSingleton() {
        AtomicInteger counter = new AtomicInteger(0);

        easyDI.bindProvider(MySingleton.class, () -> {
            counter.incrementAndGet();

            return new MySingleton();
        });

        final MySingleton firstInstance = easyDI.getInstance(MySingleton.class);
        final MySingleton secondInstance = easyDI.getInstance(MySingleton.class);

        assertThat(firstInstance).isSameAs(secondInstance);

        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("fails when provider throws an exception")
    void fail_providerThrowsException() {

        easyDI.bindProvider(ThirdParty.class, () -> {
            throw new NullPointerException("Too bad :-(");
        });

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            easyDI.getInstance(Example.class);
        });

        assertThat(exception).hasStackTraceContaining("Exception was thrown by the provider");
    }
}
