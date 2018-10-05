package eu.lestard.easydi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * This test is used to show how one can get an universal provider of instances without
 * directly injecting the EasyDI context itself.
 */
@DisplayName("InstanceProvider as alternative to context-injection") class InstanceProviderTest {

    public interface InstanceProvider {
        <T> T getInstance(Class<T> type);
    }

    public static class Example {
    }

    @Singleton
    public static class SingletonExample {
    }

    private EasyDI context;

    @BeforeEach
    void setup() {
        context = new EasyDI();
    }

    @Test
    @DisplayName("InstanceProvider works for normal class")
    void success_instanceProvider() {
        context.bindProvider(InstanceProvider.class, () -> context::getInstance);

        final InstanceProvider provider = context.getInstance(InstanceProvider.class);

        final Example example = provider.getInstance(Example.class);

        assertThat(example).isNotNull();
    }

    @Test
    @DisplayName("InstanceProvider works for singletons")
    void success_singleton() {
        context.bindProvider(InstanceProvider.class, () -> context::getInstance);


        final InstanceProvider provider = context.getInstance(InstanceProvider.class);

        final SingletonExample singleton1 = provider.getInstance(SingletonExample.class);
        final SingletonExample singleton2 = provider.getInstance(SingletonExample.class);
        final SingletonExample singleton3 = context.getInstance(SingletonExample.class);

        assertThat(singleton1).isSameAs(singleton2);
        assertThat(singleton2).isSameAs(singleton3);
    }

}
