package eu.lestard.easydi;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Constructors")
public class ConstructorsTest {

    private EasyDI easyDI;

    @BeforeEach
    void setup() throws Exception {
        easyDI = new EasyDI();
    }

    @Test
    @DisplayName("Class with public no-arg-constructor works")
    public void success_publicNoArgConstructor() {
        class Example {
            public Example() {
            }
        }

        final Example instance = easyDI.getInstance(Example.class);
        assertThat(instance).isNotNull();
    }

    @Nested
    @DisplayName("multiple constructors")
    public class MultipleConstructorsTest {

        @Test
        @DisplayName("fails without annotation")
        void fail_multipleConstructors_NotAnnotated() {
            class Example {
                public Example() {
                }

                public Example(String helloworld) {
                }
            }

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                easyDI.getInstance(Example.class);
            });

            assertThat(exception).hasStackTraceContaining("more than one public constructor defined");
        }

        @Test
        @DisplayName("fails with multiple annotations")
        void fail_multipleConstructors_TwoWithAnnotation() {
            class Example {
                @Inject
                public Example() {
                }

                @Inject
                public Example(String helloworld) {
                }
            }

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                easyDI.getInstance(Example.class);
            });

            assertThat(exception).hasStackTraceContaining("@Inject");
        }


        @Test
        @DisplayName("works with exactly one annotation")
        void success_multipleConstructors_OneWithAnnotation() {
            class Example {
                @Inject
                public Example() {
                }

                public Example(String helloWorld) {
                }
            }

            final Example instance = easyDI.getInstance(Example.class);
            assertThat(instance).isNotNull();
        }

        @Test
        @DisplayName("works with only one public constructor")
        void success_multipleConstructors_OnlyOnePublic() {
            class Example {
                public Example() {
                }

                Example(String helloWorld) {
                }
            }

            final Example instance = easyDI.getInstance(Example.class);
            assertThat(instance).isNotNull();
        }
    }


    @Test
    @DisplayName("fail without public constructor")
    void fail_noDeclaredConstructor() {
        class Example {
            private Example() {
            }
        }

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            easyDI.getInstance(Example.class);
        });

        assertThat(exception).hasStackTraceContaining("no public constructor");
    }

    @Test
    @DisplayName("works with constructor with one dependency")
    void success_constructorWithOneDependency() {
        class Dependency {
            public Dependency() {
            }
        }

        class Example {
            private final Dependency dependency;

            public Example(Dependency dependency) {
                this.dependency = dependency;
            }
        }

        final Example instance = easyDI.getInstance(Example.class);
        assertThat(instance).isNotNull();
        assertThat(instance.dependency).isNotNull();
    }

    @Test
    @DisplayName("fails for class that depends on itself")
    void fail_recursiveConstructorArguments() {
        class Example {
            public Example(Example example) {
            }
        }

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            easyDI.getInstance(Example.class);
        });

        assertThat(exception).hasStackTraceContaining("cyclic dependency");
    }

    @Test
    @DisplayName("fails when constructor throws an exception")
    void fail_exceptionInConstructor() {
        class Example {
            public Example() {
                throw new NullPointerException();
            }
        }

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            easyDI.getInstance(Example.class);
        });

        assertThat(exception).hasStackTraceContaining("Exception was thrown during the instantiation");
    }

}
