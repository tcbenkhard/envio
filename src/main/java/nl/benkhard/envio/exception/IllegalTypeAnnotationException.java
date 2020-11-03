package nl.benkhard.envio.exception;

import javax.lang.model.element.ElementKind;

public class IllegalTypeAnnotationException extends RuntimeException {

    private final static String MESSAGE = "EnvironmentVariable annotation is only allowed on classes and methods. Violation on element '%s'.";

    public IllegalTypeAnnotationException(String name) {
        super(String.format(MESSAGE, name));
    }
}
