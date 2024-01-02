package exceptions;

public class InvalidTagNameException extends RuntimeException{
    public InvalidTagNameException(String message) {
        super(message);
    }
}
