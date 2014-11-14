package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;

import static org.assertj.core.api.Assertions.*;

public class InjectedProviderTest {


    public static class MyDependency {
        public static boolean constructorCalled = false;

        public MyDependency(){
            constructorCalled = true;
        }
    }

    public static class MyClass {
        public Provider<MyDependency> provider;

        public MyClass(Provider<MyDependency> provider){
            this.provider = provider;
        }
    }

    public static class MyFailClass {

        public MyFailClass(Provider provider){
        }
    }

    private EasyDI easyDI;

    @Before
    public void setup(){
        easyDI = new EasyDI();
    }

    @Test
    public void success_provider(){
        MyDependency.constructorCalled = false;

        final MyClass myClass = easyDI.getInstance(MyClass.class);

        assertThat(myClass.provider).isNotNull();
        assertThat(MyDependency.constructorCalled).isFalse();

        final MyDependency myDependency = myClass.provider.get();
        assertThat(MyDependency.constructorCalled).isTrue();
        assertThat(myDependency).isNotNull();
    }

    @Test(expected = IllegalStateException.class)
    public void fail_provider_without_type(){
        easyDI.getInstance(MyFailClass.class);
    }
}
