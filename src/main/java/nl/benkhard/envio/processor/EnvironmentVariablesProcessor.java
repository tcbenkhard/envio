package nl.benkhard.envio.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import lombok.SneakyThrows;
import nl.benkhard.envio.annotation.EnvironmentVariable;
import nl.benkhard.envio.annotation.EnvironmentVariables;
import nl.benkhard.envio.builder.EnvironmentClassTypeSpecBuilder;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("nl.benkhard.envio.annotation.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class EnvironmentVariablesProcessor extends AbstractProcessor {

    @SneakyThrows
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        EnvironmentClassTypeSpecBuilder builder = new EnvironmentClassTypeSpecBuilder();

        for(TypeElement annotation : annotations) {
            for(Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                System.out.println(String.format("Processing: %s on %s", annotation.getSimpleName(), element.getSimpleName()));
                if(annotation.getSimpleName().toString().equals("EnvironmentVariables")) {
                    processMultipleVariables(builder, annotation, element);
                } else {
                    processSingleVariable(builder, annotation, element);
                }
            }
        }

        if(builder.getVariableCount() == 0) return true;

        JavaFile.builder("nl.benkhard.envio", builder.build())
                .build()
                .writeTo(processingEnv.getFiler());

        return true;
    }

    private void processMultipleVariables(EnvironmentClassTypeSpecBuilder builder, TypeElement annotation, Element element) {
        EnvironmentVariables environmentVariables = element.getAnnotation(EnvironmentVariables.class);
        if(element instanceof TypeElement) {
            builder.addTypeAnnotations(environmentVariables.value());
        } else if(element instanceof ExecutableElement) {
            builder.addExecutableAnnotations(environmentVariables.value(), (ExecutableElement)element);
        }
    }

    private void processSingleVariable(EnvironmentClassTypeSpecBuilder builder, TypeElement annotation, Element element) {
        EnvironmentVariable environmentVariable = element.getAnnotation(EnvironmentVariable.class);
        if(element instanceof TypeElement) {
            builder.addTypeAnnotation(environmentVariable);
        } else if(element instanceof ExecutableElement) {
            builder.addExecutableAnnotation(environmentVariable, (ExecutableElement)element);
        }
    }
}
