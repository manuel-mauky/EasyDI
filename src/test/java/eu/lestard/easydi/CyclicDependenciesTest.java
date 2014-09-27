package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

public class CyclicDependenciesTest {

    public static class Root{
        public Root(DepOne depOne){}
    }

    public static class DepOne{
        public DepOne(DepTwo depTwo){}
    }

    public static class DepTwo{
        public DepTwo(Root root){}
    }

    private EasyDI easyDI;

    @Before
    public void setup() throws Exception{
        easyDI = new EasyDI();
    }

    @Test(expected = IllegalStateException.class)
    public void fail_cyclicDependencies(){
        easyDI.getInstance(Root.class);
    }


}
