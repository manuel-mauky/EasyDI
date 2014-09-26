package eu.lestard.easydi;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EasyDI {

    public <T> T getInstance(Class<T> clazz){

        final Constructor<?>[] constructors = clazz.getConstructors();

        if(constructors.length == 0){
            throw new IllegalStateException("EasyDI can't create an instance of the class [" + clazz + "]. " +
                "The class has no public constructor.");
        }


        final Constructor<?> constructor;

        if(constructors.length > 1){

            final List<Constructor<?>> constructorsWithInject = Arrays.stream(constructors).filter(c -> c.isAnnotationPresent(Inject.class)).collect(Collectors.toList());

            if(constructorsWithInject.size() != 1){
                throw new IllegalStateException("EasyDI can't create an instance of the class [" + clazz + "]. " +
                    "There are more than one public constructors so I don't know which to use. " +
                    "Fix this by either make only one constructor public " +
                    "or annotate exactly one constructor with the javax.inject.Inject annotation.");
            }

            constructor = constructorsWithInject.get(0);
        }else{
            constructor = constructors[0];
        }


        try {
            return (T)newInstance(constructor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("Something went wrong :-(");
    }


    /**
     * This method is available for testing purposes. When you want to create instances with {@link java.lang.reflect.Constructor#newInstance(Object...)}
     * of inner classes (like we are doing in our tests) you have to use the outer instance (the instance of the Test class) as first param.
     */
    Object newInstance(Constructor<?> constructor, Object...args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return constructor.newInstance(args);
    }
}
