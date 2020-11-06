package nl.benkhard.envio.validator;

import nl.benkhard.envio.exception.IllegalTypeAnnotationException;
import nl.benkhard.envio.exception.InaccessibleMethodException;
import nl.benkhard.envio.exception.ValidationException;
import nl.benkhard.envio.model.DeclaredVariable;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

public class DeclaredVariableValidator {

    public static void validate(DeclaredVariable variable) throws ValidationException {
        if(variable.getElement() instanceof TypeElement) {
            validateClass(variable);
        } else {
            validateMethod(variable);
        }
    }

    public static void validateAll(List<DeclaredVariable> variables) throws Exception {
        for(DeclaredVariable variable : variables) {
            validate(variable);
        }
    }

    private static void validateMethod(DeclaredVariable variable) throws ValidationException {
        ExecutableElement element = (ExecutableElement) variable.getElement();

        if(!element.getModifiers().containsAll(Arrays.asList(Modifier.STATIC, Modifier.PUBLIC)))
            throw new InaccessibleMethodException(element.getSimpleName().toString());

        if(element.getParameters().size() != 1 && !element.getParameters().get(0).asType().toString().equals("String"))
            throw new InvalidParameterException(
                    String.format("Methods annotated with EnvironmentVariable can have only 1 String as a parameter! " +
                            "Violation on method '%s'.", element.getSimpleName()));
    }

    private static void validateClass(DeclaredVariable variable) throws ValidationException{
        TypeElement element = (TypeElement) variable.getElement();

        if(!variable.getElement().getKind().isClass())
            throw new IllegalTypeAnnotationException(
                    ((TypeElement)variable.getElement()).getQualifiedName().toString());
    }
}
