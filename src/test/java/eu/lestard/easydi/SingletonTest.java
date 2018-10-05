package eu.lestard.easydi;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Singletons")
public class SingletonTest {

    public interface MyInterface {
    }

    private EasyDI easyDI;

    @BeforeEach
    void setup() {
        easyDI = new EasyDI();
    }

    @Nested
    @DisplayName("non-singleton counter-examples")
    public class NonSingletonTest {

        @Test
        @DisplayName("non-singleton creates new instances everytime")
        void success_nonSingleton_newInstanceEverytime() {
            class Example {
                public Example() {
                }
            }

            final Example instanceOne = easyDI.getInstance(Example.class);
            final Example instanceTwo = easyDI.getInstance(Example.class);

            assertThat(instanceOne).isNotNull().isNotSameAs(instanceTwo);
        }


        @Test
        @DisplayName("non-singleton creates new instances everytime - bigger example")
        void success_nonSingleton_biggerExample() {
            class Dependency {
                public Dependency() {
                }
            }

            class Other {
                private final Dependency dep;

                public Other(Dependency dep) {
                    this.dep = dep;
                }
            }

            class Example {
                private final Dependency dep;
                private final Other other;

                public Example(Dependency dep, Other other) {
                    this.dep = dep;
                    this.other = other;
                }
            }

            final Example instance = easyDI.getInstance(Example.class);

            assertThat(instance.dep).isNotSameAs(instance.other.dep);
        }
    }

    @Nested
    @DisplayName("@Singleton annotation")
    public class AtSingletonAnnotationTest {

        @Test
        @DisplayName("works")
        void success_singleton() {
            @Singleton class Example {
                public Example() {
                }
            }

            final Example instanceOne = easyDI.getInstance(Example.class);
            final Example instanceTwo = easyDI.getInstance(Example.class);

            assertThat(instanceOne).isSameAs(instanceTwo);
        }

        @Test
        @DisplayName("works - bigger example")
        void success_singleton_biggerExample() {
            @Singleton class Dependency {
                public Dependency() {
                }
            }

            class Other {
                private final Dependency dep;

                public Other(Dependency dep) {
                    this.dep = dep;
                }
            }

            class Example {
                private final Dependency dep;
                private final Other other;

                public Example(Dependency dep, Other other) {
                    this.dep = dep;
                    this.other = other;
                }
            }

            final Dependency dependency = easyDI.getInstance(Dependency.class);
            final Example instance = easyDI.getInstance(Example.class);

            assertThat(dependency).isSameAs(instance.dep).isSameAs(instance.other.dep);
        }
    }

    @Nested
    @DisplayName("markAsSingleton method")
    public class MarkAsSingletonTest {

        @Test
        @DisplayName("works")
        void success_singleton_noAnnotationButMarkedPerMethod() {
            class Example {
                public Example() {
                }
            }

            easyDI.markAsSingleton(Example.class);

            final Example instanceOne = easyDI.getInstance(Example.class);
            final Example instanceTwo = easyDI.getInstance(Example.class);

            assertThat(instanceOne).isNotNull().isSameAs(instanceTwo);
        }


        @Test
        @DisplayName("fails when param is an interface")
        void fail_markAsSingleton_paramIsAnInterface() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                easyDI.markAsSingleton(BindProviderTest.MyInterface.class);
            });

            assertThat(exception).hasStackTraceContaining("type is an interface");
        }
    }

}
