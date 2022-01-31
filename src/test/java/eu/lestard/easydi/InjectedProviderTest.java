package eu.lestard.easydi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.inject.Provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Injection of Provider")
class InjectedProviderTest {


    public static class MyDependency {
        static boolean constructorCalled = false;

        public MyDependency() {
            constructorCalled = true;
        }
    }

    public static class MyClass {
        Provider<MyDependency> provider;

        public MyClass(Provider<MyDependency> provider) {
            this.provider = provider;
        }
    }

    public static class MyFailClass {

        public MyFailClass(Provider provider) {
        }
    }

    private EasyDI easyDI;

    @BeforeEach
    void setup() {
        easyDI = new EasyDI();
    }

    @Test
    @DisplayName("works. Constructor of provided class is called lazily")
    void success_provider() {
        MyDependency.constructorCalled = false;

        final MyClass myClass = easyDI.getInstance(MyClass.class);

        assertThat(myClass.provider).isNotNull();
        assertThat(MyDependency.constructorCalled).isFalse();

        final MyDependency myDependency = myClass.provider.get();
        assertThat(MyDependency.constructorCalled).isTrue();
        assertThat(myDependency).isNotNull();
    }

    @Test
    @DisplayName("fails when no Type is declared for Provider")
    void fail_provider_without_type() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            easyDI.getInstance(MyFailClass.class);
        });

        assertThat(exception).hasStackTraceContaining("Provider without a type parameter");
    }
}
