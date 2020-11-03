package nl.benkhard.envio.validator;

import com.sun.tools.javac.util.List;
import nl.benkhard.envio.exception.InaccessibleMethodException;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.security.InvalidParameterException;

public class MethodValidator {

    public static void validate(ExecutableElement element) {
        if(!element.getModifiers().containsAll(List.of(Modifier.STATIC, Modifier.PUBLIC)))
            throw new InaccessibleMethodException(element.getSimpleName().toString());

        if(element.getParameters().size() != 1 && !element.getParameters().get(0).asType().toString().equals("String"))
            throw new InvalidParameterException(
                    String.format("Methods annotated with EnvironmentVariable can have only 1 String as a parameter! " +
                            "Violation on method '%s'.", element.getSimpleName()));
    }
}
