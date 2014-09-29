package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Singleton;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

public class ProviderTest {

    public static class ThirdParty {
        private ThirdParty(){}

        public static ThirdParty thirdPartyFactory(){
            return new ThirdParty();
        }
    }

    public static interface MyInterface{
    }

    @Singleton
    public static class MySingleton{}

    public static class Example{
        final ThirdParty dep;

        public Example(ThirdParty dep){
            this.dep = dep;
        }
    }



    private EasyDI easyDI;

    @Before
    public void setup() throws Exception{
        easyDI = new EasyDI();
    }

    @Test
    public void success_providerIsAvailable(){
        easyDI.bindProvider(ThirdParty.class, ThirdParty::thirdPartyFactory);

        final Example instance = easyDI.getInstance(Example.class);
        assertThat(instance).isNotNull();
        assertThat(instance.dep).isNotNull();
    }

    @Test
    public void success_providerForInterface(){
        // the provider creates an anonymous inner class for the interface
        easyDI.bindProvider(MyInterface.class, ()-> new MyInterface() {});

        // now it is working
        final MyInterface instance = easyDI.getInstance(MyInterface.class);
        assertThat(instance).isNotNull();
    }

    @Test
    public void success_providerAndSingleton(){
        AtomicInteger counter = new AtomicInteger(0);

        easyDI.bindProvider(MySingleton.class, ()-> {
            counter.incrementAndGet();

            return new MySingleton();
        });

        final MySingleton firstInstance = easyDI.getInstance(MySingleton.class);
        final MySingleton secondInstance = easyDI.getInstance(MySingleton.class);

        assertThat(firstInstance).isSameAs(secondInstance);

        assertThat(counter.get()).isEqualTo(1);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_providerThrowsException(){

        easyDI.bindProvider(ThirdParty.class, ()->{
            throw new NullPointerException("Too bad :-(");
        });

        easyDI.getInstance(Example.class);
    }
}
