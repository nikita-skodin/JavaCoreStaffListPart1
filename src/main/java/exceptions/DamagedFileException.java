package exceptions;

public class DamagedFileException extends RuntimeException{
    public DamagedFileException(String message) {
        super(message);
    }
}
