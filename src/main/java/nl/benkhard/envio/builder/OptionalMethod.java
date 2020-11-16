package nl.benkhard.envio.builder;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import lombok.SneakyThrows;
import nl.benkhard.envio.model.DeclaredVariable;
import nl.benkhard.envio.util.NameUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Optional;

public class OptionalMethod {

    public static MethodSpec from(DeclaredVariable variable) {
        if(variable.getElement() instanceof TypeElement) {
            return buildVariable(variable);
        } else {
            return buildWrappedVariable(variable);
        }
    }

    @SneakyThrows
    private static MethodSpec buildWrappedVariable(DeclaredVariable variable) {
        ExecutableElement method = (ExecutableElement) variable.getElement();
        MethodSpec.Builder builder = MethodSpec.methodBuilder(NameUtils.getMethodName(variable));
        builder.returns(ParameterizedTypeName.get(Optional.class, Class.forName(method.getReturnType().toString())));
        builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builder.addStatement(String.format("String systemValue = System.getenv(\"%s\")", variable.getName()))
                .beginControlFlow(String.format("if(systemValue != null && systemValue.length() > 0)"))
                .addStatement(String.format("return Optional.empty()"))
                .endControlFlow()
                .addStatement(String.format("%s parsedValue = %s.%s(systemValue)"
                        , method.getReturnType().toString()
                        , ((TypeElement) method.getEnclosingElement()).getQualifiedName()
                        , method.getSimpleName()))
                .addStatement(String.format("return Optional.ofNullable(parsedValue)"));

        return builder.build();
    }

    private static MethodSpec buildVariable(DeclaredVariable variable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(NameUtils.getMethodName(variable));
        builder.returns(ParameterizedTypeName.get(Optional.class, String.class));
        builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builder.addStatement(String.format("return Optional.ofNullable(System.getenv(\"%s\"))", variable.getName()));

        return builder.build();
    }
}
