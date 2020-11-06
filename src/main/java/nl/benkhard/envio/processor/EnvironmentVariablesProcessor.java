package nl.benkhard.envio.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import lombok.SneakyThrows;
import nl.benkhard.envio.annotation.EnvironmentVariable;
import nl.benkhard.envio.annotation.EnvironmentVariables;
import nl.benkhard.envio.builder.EnvironmentClassTypeSpecBuilder;
import nl.benkhard.envio.model.DeclaredVariable;
import nl.benkhard.envio.validator.DeclaredVariableValidator;
import nl.benkhard.envio.validator.MethodValidator;
import nl.benkhard.envio.validator.TypeValidator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("nl.benkhard.envio.annotation.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class EnvironmentVariablesProcessor extends AbstractProcessor {

    @SneakyThrows
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        EnvironmentClassTypeSpecBuilder builder = new EnvironmentClassTypeSpecBuilder();
        List<DeclaredVariable> declaredVariables = new ArrayList<>();

        for(TypeElement annotation : annotations) {
            for(Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                List<DeclaredVariable> extractedVariables = extractDeclaredVariables(annotation, element);
                declaredVariables.addAll(extractedVariables);
            }
        }

        DeclaredVariableValidator.validateAll(declaredVariables);
        // build class from declaredVariables
            // processVariables(builder, element, environmentVariables.value());
        // write to file

        if(builder.getVariableCount() == 0) return true;

        JavaFile.builder("nl.benkhard.envio", builder.build())
                .build()
                .writeTo(processingEnv.getFiler());

        return true;
    }

    private List<DeclaredVariable> extractDeclaredVariables(TypeElement annotation, Element element) {
        if(annotation.getSimpleName().toString().equals(EnvironmentVariables.class.getSimpleName())) {
            return Arrays.stream(element.getAnnotation(EnvironmentVariables.class).value())
                    .map(variable -> DeclaredVariable.from(variable, element))
                    .collect(Collectors.toList());
        } else {
            return Arrays.asList(DeclaredVariable.from(element.getAnnotation(EnvironmentVariable.class), element));
        }
    }
}
