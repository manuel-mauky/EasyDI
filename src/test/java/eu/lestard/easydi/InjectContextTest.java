package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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
            Context context;
            Dep dep;

            public Example(Context context){
                this.context = context;
                this.dep = context.getInstance(Dep.class);
            }
        }


        final Example instance = easyDI.getInstance(Example.class);

        assertThat(instance).isNotNull();

        assertThat(instance.context).isNotNull();
        assertThat(instance.dep).isNotNull();
    }

    @Test
    public void success_context_cannot_be_casted_to_EasyDI(){
        final Context context = easyDI.getInstance(Context.class);
        assertThat(context).isNotInstanceOf(EasyDI.class);

        try{
            EasyDI castedEasyDI = (EasyDI) context;
            fail("A ClassCastException was expected!");
        }catch (ClassCastException exception){
            // expected
        }
    }

    @Test
    public void success_context_is_a_singleton(){
        final Context context1 = easyDI.getInstance(Context.class);
        final Context context2 = easyDI.getInstance(Context.class);

        assertThat(context1).isSameAs(context2);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_endless_recursive_injection(){

        class Example {
            public Example(Context context){
                context.getInstance(Example.class);
            }
        }

        easyDI.getInstance(Example.class);
    }
}
