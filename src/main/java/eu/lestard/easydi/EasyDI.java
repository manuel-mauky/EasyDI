package eu.lestard.easydi;

import javax.inject.Inject;
import javax.inject.Singleton;
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

    /**
     * A map with all classes that are marked as singleton and the actual singleton instance.
     */
    private Map<Class, Object> singletons = new HashMap<>();

    /**
     * Get an instance of the given class type.
     *
     * @param type the class type of which an instance is retrieved.
     * @param <T>  the generic type of the class.
     * @return an instance of the given type.
     */
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> type) {

        // If a class was already requested before...
        if (requestedClasses.contains(type)) {
            // ... we should have been able to instantiate it in the past ...
            if (!instantiableClasses.contains(type)) {

                // if not, this means a cyclic dependency and is an error
                throw new IllegalStateException("");
            }
        } else {
            // if this class wasn't requested before we now add it to the checklist.
            requestedClasses.add(type);
        }

        if(singletons.containsKey(type)){
            return (T) singletons.get(type);
        }

        final Constructor<T> constructor = findConstructor(type);

        final Parameter[] parameters = constructor.getParameters();

        // recursively get of all constructor arguments
        final List<Object> arguments = Arrays.stream(parameters)
            .map(p -> (Object) getInstance(p.getType()))
            .collect(Collectors.toList());

        try {
            final T newInstance = constructor.newInstance(arguments.toArray());
            instantiableClasses.add(type); // mark the class as successfully instantiable.

            // when the class is marked as singleton it's instance is now added to the singleton map
            if(type.isAnnotationPresent(Singleton.class)){
                singletons.put(type, newInstance);
            }

            return newInstance;
        } catch (Exception e) {
            throw new IllegalStateException(createErrorMessageStart(type) + "An Exception was thrown while the instantiation.", e);
        }
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
     * @param type the class of which the constructor is searched for.
     * @param <T>  the generic type of the class.
     * @return the constructor to use
     * @throws java.lang.IllegalStateException when no constructor can be found.
     */
    @SuppressWarnings("unchecked")
    private <T> Constructor<T> findConstructor(Class<T> type) {
        final Constructor<?>[] constructors = type.getConstructors();

        if (constructors.length == 0) {
            throw new IllegalStateException(createErrorMessageStart(type) +
                "The class has no public constructor.");
        }

        if (constructors.length > 1) {

            final List<Constructor<?>> constructorsWithInject = Arrays
                .stream(constructors)
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());

            if (constructorsWithInject.size() != 1) {
                throw new IllegalStateException(createErrorMessageStart(type) +
                    "There are more than one public constructors so I don't know which to use. " +
                    "Fix this by either make only one constructor public " +
                    "or annotate exactly one constructor with the javax.inject.Inject annotation.");
            }

            // we are not modifying the constructor array so we can safely cast here.
            return (Constructor<T>) constructorsWithInject.get(0);
        } else {
            return (Constructor<T>) constructors[0];
        }
    }

    /**
     * We need this string for most error messages.
     */
    private String createErrorMessageStart(Class type) {
        return "EasyDI can't create an instance of the class [" + type + "]. ";
    }
}
