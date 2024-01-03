package exceptions;

public class IncorrectContentException extends RuntimeException{
    public IncorrectContentException(String message) {
        super(message);
    }
}
