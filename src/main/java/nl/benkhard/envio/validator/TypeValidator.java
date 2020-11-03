package nl.benkhard.envio.validator;

import nl.benkhard.envio.exception.IllegalTypeAnnotationException;

import javax.lang.model.element.TypeElement;

public class TypeValidator {

    public static void validate(TypeElement element) {
        // should be on a class, not an interface
        if(!element.getKind().isClass())
            throw new IllegalTypeAnnotationException(element.getQualifiedName().toString());
    }
}
