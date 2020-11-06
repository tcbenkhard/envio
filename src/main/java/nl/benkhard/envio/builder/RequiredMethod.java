package nl.benkhard.envio.builder;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import nl.benkhard.envio.model.DeclaredVariable;
import nl.benkhard.envio.util.NameUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class RequiredMethod {

    public static MethodSpec from(DeclaredVariable variable) {
        if(variable.getElement() instanceof TypeElement) {
            return buildVariable(variable);
        } else {
            return buildWrappedVariable(variable);
        }
    }

    private static MethodSpec buildWrappedVariable(DeclaredVariable variable) {
        ExecutableElement method = (ExecutableElement) variable.getElement();
        MethodSpec.Builder builder = MethodSpec.methodBuilder(NameUtils.getMethodName(variable));
        builder.returns(TypeName.get(method.getReturnType()));
        builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builder.addStatement(String.format("String systemValue = System.getenv(\"%s\")", variable.getName()))
                .addStatement(String.format("%s parsedValue = %s.%s(systemValue)"
                        , method.getReturnType().toString()
                        , ((TypeElement) method.getEnclosingElement()).getQualifiedName()
                        , method.getSimpleName()))
                .addStatement(String.format("return parsedValue"));

        return builder.build();
    }

    private static MethodSpec buildVariable(DeclaredVariable variable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(NameUtils.getMethodName(variable));
        builder.returns(String.class);
        builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builder.addStatement(String.format("return System.getenv(\"%s\")", variable.getName()));

        return builder.build();
    }
}
