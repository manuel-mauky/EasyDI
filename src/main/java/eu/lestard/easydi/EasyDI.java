package eu.lestard.easydi;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class EasyDI {


    /**
     * A checklist for all class types that were requested to get instances from.
     */
    private Set<Class> requestedClasses = new HashSet<>();

    /**
     * A checklist with all class types that were successfully instantiated.
     */
    private Set<Class> instantiableClasses = new HashSet<>();


    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz){

        // If a class was already requested before...
        if(requestedClasses.contains(clazz)){
            // ... we should have been able to instantiate it in the past ...
            if(!instantiableClasses.contains(clazz)){

                // if not, this means a cyclic dependency and is an error
                throw new IllegalStateException("");
            }
        }else{
            // if this class wasn't requested before we now add it to the checklist.
            requestedClasses.add(clazz);
        }


        final Constructor<?> constructor = findConstructor(clazz);

        final Parameter[] parameters = constructor.getParameters();

        // recursively get of all constructor arguments
        final List<Object> arguments = Arrays.stream(parameters)
            .map(p -> (Object) getInstance(p.getType()))
            .collect(Collectors.toList());

        try {

            final Object newInstance = constructor.newInstance(arguments.toArray());
            instantiableClasses.add(clazz); // mark the class as successfully instantiable.
            return (T) newInstance;
        } catch (Exception e) {
            System.out.println("e");
            e.printStackTrace();
        }

        throw new IllegalStateException("Something went wrong :-(");
    }

    /**
     * Find out the constructor that will be used for instantiation.
     * <br>
     * If there is only one public constructor, it will be used.
     * <br>
     * If there are more then one public constructors, the one with an {@link javax.inject.Inject}
     * annotation is used.
     *
     * <br>
     * In all other cases an {@link java.lang.IllegalStateException} is thrown.
     *
     * @param clazz the class of which the constructor is searched for.
     * @param <T> the generic type of the class.
     * @return the constructor to use
     *
     * @throws java.lang.IllegalStateException when no constructor can be found.
     */
    private <T> Constructor<?> findConstructor(Class<T> clazz) {
        final Constructor<?>[] constructors = clazz.getConstructors();

        if(constructors.length == 0){
            throw new IllegalStateException("EasyDI can't create an instance of the class [" + clazz + "]. " +
                "The class has no public constructor.");
        }

        if(constructors.length > 1){

            final List<Constructor<?>> constructorsWithInject = Arrays
                .stream(constructors)
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());

            if(constructorsWithInject.size() != 1){
                throw new IllegalStateException("EasyDI can't create an instance of the class [" + clazz + "]. " +
                    "There are more than one public constructors so I don't know which to use. " +
                    "Fix this by either make only one constructor public " +
                    "or annotate exactly one constructor with the javax.inject.Inject annotation.");
            }

            return constructorsWithInject.get(0);
        }else{
            return constructors[0];
        }
    }
}
