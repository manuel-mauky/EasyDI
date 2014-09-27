package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.*;

public class EasyDITest {

    private EasyDI easyDI;

    @Before
    public void setup() throws Exception{
        easyDI = new EasyDI();
    }

    @Test
    public void success_publicNoArgConstructor(){
        class Example {
            public Example(){
            }
        }

        final Example instance = easyDI.getInstance(Example.class);
        assertThat(instance).isNotNull();
    }

    @Test(expected = IllegalStateException.class)
    public void fail_multipleConstructors_NotAnnotated(){
        class Example{
            public Example(){
            }

            public Example(String helloworld){
            }
        }

        easyDI.getInstance(Example.class);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_multipleConstructors_TwoWithAnnotation(){
        class Example{
            @Inject
            public Example(){
            }

            @Inject
            public Example(String helloworld){
            }
        }

        easyDI.getInstance(Example.class);
    }


    @Test
    public void success_multipleConstructors_OneWithAnnotation(){
        class Example {
            @Inject
            public Example(){
            }

            public Example(String helloWorld){
            }
        }

        final Example instance = easyDI.getInstance(Example.class);
        assertThat(instance).isNotNull();
    }

    @Test
    public void success_multipleConstructors_OnlyOnePublic(){
        class Example {
            public Example(){
            }

            Example(String helloWorld){
            }
        }

        final Example instance = easyDI.getInstance(Example.class);
        assertThat(instance).isNotNull();
    }



    @Test(expected = IllegalStateException.class)
    public void fail_noDeclaredConstructor(){
        class Example {
            private Example(){
            }
        }

        easyDI.getInstance(Example.class);
    }

    @Test
    public void success_constructorWithOneDependency(){
        class Dependency {
            public Dependency(){
            }
        }

        class Example{
            final Dependency dependency;

            public Example(Dependency dependency){
                this.dependency = dependency;
            }
        }

        final Example instance = easyDI.getInstance(Example.class);
        assertThat(instance).isNotNull();
        assertThat(instance.dependency).isNotNull();
    }

    @Test(expected = IllegalStateException.class)
    public void fail_recursiveConstructorArguments(){
        class Example{
            public Example(Example example){
            }
        }

        easyDI.getInstance(Example.class);
    }
}
