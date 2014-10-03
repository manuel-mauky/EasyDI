package eu.lestard.easydi;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IsAbstractClassHelperTest {

    public static class MyClass {}

    public static interface MyInterface {}

    public static abstract class MyAbstractClass {}


    @Test
    public void test_isAbstractClass(){
        assertThat(EasyDI.isAbstractClass(MyClass.class)).isFalse();
        assertThat(EasyDI.isAbstractClass(MyInterface.class)).isFalse();
        assertThat(EasyDI.isAbstractClass(MyAbstractClass.class)).isTrue();
    }

}
