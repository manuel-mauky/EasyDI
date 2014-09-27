package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NoCyclicDependenciesTest {

    public static class Root {
        public Root(DepOne depOne, DepThree depThree){}
    }

    public static class DepOne{
        public DepOne(DepTwo depTwo, DepThree depThree){}
    }

    public static class DepTwo{
        public DepTwo(DepThree depThree){}
    }

    public static class DepThree{
        public DepThree(){}
    }


    private EasyDI easyDI;

    @Before
    public void setup() throws Exception{
        easyDI = new EasyDI();
    }

    @Test
    public void success_NoCyclicDependencies(){
        final Root instance = easyDI.getInstance(Root.class);
        assertThat(instance).isNotNull();
    }

}
