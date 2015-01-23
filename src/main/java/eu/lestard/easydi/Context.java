package eu.lestard.easydi;

public interface Context {

    <T> T getInstance(Class<T> requestedType);

}
