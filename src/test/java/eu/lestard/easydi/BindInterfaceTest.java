package eu.lestard.easydi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This test is used to verify the behaviour of EasyDI dealing with interfaces.
 */
@DisplayName("Bind Interface") class BindInterfaceTest {

    interface A {
    }

    interface B {
    }

    public static class ExampleOne implements A {
    }

    public static class ExampleTwo implements A, B {
    }


    private EasyDI easyDI;

    @BeforeEach
    void setup() {
        easyDI = new EasyDI();
    }


    @Test
    @DisplayName("works with a correct binding")
    void success_withBinding() {
        easyDI.bindInterface(A.class, ExampleOne.class);

        final A instance = easyDI.getInstance(A.class);
        assertThat(instance).isNotNull().isInstanceOf(ExampleOne.class);
    }


    @Test
    @DisplayName("fails without a binding")
    void fail_noBinding() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            easyDI.getInstance(A.class);
        });

        assertThat(exception).hasStackTraceContaining("is an interface")
            .hasStackTraceContaining("use the 'bindInterface' method");
    }

    /**
     * If two (or more) bindings for the same interface are configures,
     * then only the last one will be active. All previous bindings are overwritten.
     */
    @Test
    @DisplayName("binding of interfaces can be overwritten")
    void success_lastBindingIsUsed() {
        easyDI.bindInterface(A.class, ExampleOne.class);
        easyDI.bindInterface(A.class, ExampleTwo.class);

        final A instance = easyDI.getInstance(A.class);
        assertThat(instance).isNotNull().isInstanceOf(ExampleTwo.class);
    }



    @Singleton interface WannabeSingleton {
    }

    public static class NonSingleton implements WannabeSingleton {
    }

    /**
     * The {@link javax.inject.Singleton} annotation is ignored when it is added to
     * interfaces.
     */
    @Test
    @DisplayName("interfaces cannot be marked as singletons")
    void fail_interfacesCantBeMarkedAsSingleton() {

        easyDI.bindInterface(WannabeSingleton.class, NonSingleton.class);

        final WannabeSingleton instanceOne = easyDI.getInstance(WannabeSingleton.class);
        final WannabeSingleton instanceTwo = easyDI.getInstance(WannabeSingleton.class);

        assertThat(instanceOne).isNotSameAs(instanceTwo);
    }

    @Test
    @DisplayName("fails when param is not an interface")
    void fail_bindInterface_paramIsNotAnInterface() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            easyDI.bindInterface(ExampleOne.class, ExampleOne.class);
        });

        assertThat(exception).hasStackTraceContaining("not an interface");
    }


    interface SuperInterface {
    }

    interface SubInterface extends SuperInterface {
    }

    @Test
    @DisplayName("fails when second param is also an interface")
    void fail_bindInterface_secondParamIsInterface() {
        assertThrows(IllegalArgumentException.class, () -> {
            easyDI.bindInterface(SuperInterface.class, SubInterface.class);
        });
    }


    static abstract class AbstractA implements A {
    }

    @Test
    @DisplayName("fails when second param is an abstract class")
    void fail_bindInterface_secondParamIsAbstractClass() {
        assertThrows(IllegalArgumentException.class, () -> {
            easyDI.bindInterface(A.class, AbstractA.class);
        });
    }
}
