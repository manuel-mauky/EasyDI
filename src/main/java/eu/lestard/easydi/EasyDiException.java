package eu.lestard.easydi;

public class EasyDiException extends RuntimeException {

    public EasyDiException(String message, Throwable cause){
        super(message, cause);
    }

    public EasyDiException(String message){
        super(message);
    }
}
