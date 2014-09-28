package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    @Test(expected = IllegalStateException.class)
    public void fail_exceptionInConstructor(){
        class Example{
            public Example(){
                throw new NullPointerException();
            }
        }

        easyDI.getInstance(Example.class);
    }

    @Test
    public void success_nonSingleton_newInstanceEverytime(){
        class Example {
            public Example(){}
        }

        final Example instanceOne = easyDI.getInstance(Example.class);
        final Example instanceTwo = easyDI.getInstance(Example.class);

        assertThat(instanceOne).isNotNull().isNotSameAs(instanceTwo);
    }

    @Test
    public void success_nonSingleton_biggerExample(){
        class Dependency{
            public Dependency(){}
        }

        class Other {
            final Dependency dep;

            public Other(Dependency dep){
                this.dep = dep;
            }
        }

        class Example{
            public final Dependency dep;
            public final Other other;

            public Example(Dependency dep, Other other){
                this.dep = dep;
                this.other = other;
            }
        }

        final Example instance = easyDI.getInstance(Example.class);

        assertThat(instance.dep).isNotSameAs(instance.other.dep);
    }

    @Test
    public void success_singleton(){
        @Singleton
        class Example{
            public Example(){}
        }

        final Example instanceOne = easyDI.getInstance(Example.class);
        final Example instanceTwo = easyDI.getInstance(Example.class);

        assertThat(instanceOne).isSameAs(instanceTwo);
    }

    @Test
    public void success_singleton_biggerExample(){
        @Singleton
        class Dependency{
            public Dependency(){}
        }

        class Other {
            final Dependency dep;

            public Other(Dependency dep){
                this.dep = dep;
            }
        }

        class Example{
            public final Dependency dep;
            public final Other other;

            public Example(Dependency dep, Other other){
                this.dep = dep;
                this.other = other;
            }
        }

        final Dependency dependency = easyDI.getInstance(Dependency.class);
        final Example instance = easyDI.getInstance(Example.class);

        assertThat(dependency).isSameAs(instance.dep).isSameAs(instance.other.dep);
    }
}
