package nl.benkhard.envio.exception;

public class InaccessibleMethodException extends RuntimeException {
    private final static String MESSAGE = "EnvironmentVariable annotation can only be placed on public static methods! Violation on method '%s'.";
    public InaccessibleMethodException(String methodName) {
        super(String.format(MESSAGE, methodName));
    }
}
