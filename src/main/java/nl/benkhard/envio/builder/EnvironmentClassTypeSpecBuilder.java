package nl.benkhard.envio.builder;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.*;
import lombok.SneakyThrows;
import nl.benkhard.envio.annotation.EnvironmentVariable;
import nl.benkhard.envio.validator.TypeValidator;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Optional;

public class EnvironmentClassTypeSpecBuilder {

    private final String CLASSNAME = "Environment";
    private final TypeSpec.Builder builder;
    private int variableCount = 0;

    public EnvironmentClassTypeSpecBuilder() {
        this.builder = TypeSpec.classBuilder(CLASSNAME);
    }

    public TypeSpec build() {
        builder.addModifiers(Modifier.PUBLIC);
        return builder.build();
    }

    public void addTypeAnnotations(EnvironmentVariable... variables) {
        Arrays.stream(variables).forEach(this::addTypeAnnotation);
    }

    public void addExecutableAnnotations(ExecutableElement element, EnvironmentVariable... variables) {
        Arrays.stream(variables).forEach(var -> addExecutableAnnotation(var, element));
    }

    public int getVariableCount() {
        return variableCount;
    }

    private void generateGetter(EnvironmentVariable variable) {
        TypeName returnType = variable.required() ? TypeName.get(String.class)
                : ParameterizedTypeName.get(Optional.class, String.class);

        CodeBlock codeBlock = variable.required() ? generateRequiredCodeBlock(variable) : generateOptionalCodeBlock(variable);

        builder.addMethod(buildMethodSpec(variable.name(), returnType, codeBlock));
    }

    private MethodSpec buildMethodSpec(String name, TypeName returnType, CodeBlock codeBlock) {
        String methodName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
        MethodSpec.Builder builder = MethodSpec.methodBuilder(String.format("get%s", methodName))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addCode(codeBlock)
                .returns(returnType);

        return builder.build();
    }

    private CodeBlock generateOptionalCodeBlock(EnvironmentVariable variable) {
        return CodeBlock.builder()
                .addStatement(String.format("return Optional.ofNullable(System.getenv(\"%s\"))", variable.name()))
                .build();
    }

    private CodeBlock generateRequiredCodeBlock(EnvironmentVariable variable) {
        return CodeBlock.builder()
                .addStatement(String.format("return System.getenv(\"%s\")", variable.name()))
                .build();
    }

    private void addTypeAnnotation(EnvironmentVariable environmentVariable) {
        generateGetter(environmentVariable);
        variableCount++;
    }

    private void addExecutableAnnotation(EnvironmentVariable variable, ExecutableElement element) {
        generateWrappingGetter(variable, element);
        variableCount++;
    }

    @SneakyThrows
    private void generateWrappingGetter(EnvironmentVariable variable, ExecutableElement element) {
        String methodName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, variable.name());

        TypeName returnType = variable.required()
                ? TypeName.get(element.getReturnType())
                : ParameterizedTypeName.get(Optional.class, Class.forName(element.getReturnType().toString()));

        CodeBlock codeBlock = variable.required()
                ? generateRequiredWrappingCodeBlock(variable, element)
                : generateOptionalWrappingCodeBlock(variable, element);

        builder.addMethod(buildMethodSpec(methodName, returnType, codeBlock));
    }

    private CodeBlock generateOptionalWrappingCodeBlock(EnvironmentVariable variable, ExecutableElement element) {
        return CodeBlock.builder()
                .addStatement(String.format("String systemValue = System.getenv(\"%s\")", variable.name()))
                .beginControlFlow(String.format("if(systemValue != null && systemValue.length() > 0)"))
                .addStatement(String.format("return Optional.empty()"))
                .endControlFlow()
                .addStatement(String.format("%s parsedValue = %s.%s(systemValue)"
                        , element.getReturnType().toString()
                        , ((TypeElement) element.getEnclosingElement()).getQualifiedName()
                        , element.getSimpleName()))
                .addStatement(String.format("return Optional.ofNullable(parsedValue)"))
                .build();
    }

    private CodeBlock generateRequiredWrappingCodeBlock(EnvironmentVariable variable, ExecutableElement element) {
        return CodeBlock.builder()
                .addStatement(String.format("String systemValue = System.getenv(\"%s\")", variable.name()))
                .addStatement(String.format("%s parsedValue = %s.%s(systemValue)"
                        , element.getReturnType().toString()
                        , ((TypeElement) element.getEnclosingElement()).getQualifiedName()
                        , element.getSimpleName()))
                .addStatement(String.format("return parsedValue"))
                .build();
    }
}
