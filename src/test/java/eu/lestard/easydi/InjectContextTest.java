package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InjectContextTest {
	
	private EasyDI easyDI;
	
	@Before
	public void setup() {
		easyDI = new EasyDI();
	}
	
	@Test
	public void success_injectContext() {
        class Dep {
            public Dep(){}
        }

        class Example {
            EasyDI context;
            Dep dep;

            public Example(EasyDI context){
                this.context = context;
                this.dep = context.getInstance(Dep.class);
            }
        }


        easyDI.bindProvider(EasyDI.class, ()-> easyDI);


        final Example instance = easyDI.getInstance(Example.class);

        assertThat(instance).isNotNull();

        assertThat(instance.context).isNotNull();
        assertThat(instance.dep).isNotNull();
    }

    @Test
    public void success_context_is_a_singleton(){

        easyDI.bindProvider(EasyDI.class, ()-> easyDI);

        final EasyDI context1 = easyDI.getInstance(EasyDI.class);
        final EasyDI context2 = easyDI.getInstance(EasyDI.class);

        assertThat(context1).isSameAs(context2);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_endless_recursive_injection(){

        class Example {
            public Example(EasyDI context){
                context.getInstance(Example.class);
            }
        }

        easyDI.bindProvider(EasyDI.class, ()-> easyDI);

        easyDI.getInstance(Example.class);
    }
}
