public class LengthException extends RuntimeException{
    public LengthException() {
    }

    public LengthException(String message) {
        super(message);
    }

    public LengthException(String message, Throwable cause) {
        super(message, cause);
    }

    public LengthException(Throwable cause) {
        super(cause);
    }

    public LengthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
