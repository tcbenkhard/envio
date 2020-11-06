package nl.benkhard.envio.exception;

public class InvalidParametersException extends ValidationException {
    private final static String MESSAGE = "Methods annotated with EnvironmentVariable can have only 1 String as a parameter! Violation on method '%s'.";

    public InvalidParametersException(String methodName) {
        super(String.format(MESSAGE, methodName));
    }
}
