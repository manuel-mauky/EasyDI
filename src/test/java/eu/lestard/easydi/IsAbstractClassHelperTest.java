package eu.lestard.easydi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Helper method isAbstractClass")
class IsAbstractClassHelperTest {

    private static class MyClass {
    }

    static interface MyInterface {
    }

    static abstract class MyAbstractClass {
    }


    @Test
    @DisplayName("works")
    void test_isAbstractClass() {
        assertThat(EasyDI.isAbstractClass(MyClass.class)).isFalse();
        assertThat(EasyDI.isAbstractClass(MyInterface.class)).isFalse();
        assertThat(EasyDI.isAbstractClass(MyAbstractClass.class)).isTrue();
    }

}
