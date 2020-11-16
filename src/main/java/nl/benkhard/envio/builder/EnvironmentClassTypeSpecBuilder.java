package nl.benkhard.envio.builder;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import nl.benkhard.envio.model.DeclaredVariable;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.List;

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

    public void addDeclaredVariables(List<DeclaredVariable> declaredVariables) {
        declaredVariables.forEach(this::createVariable);
        createStaticBlock(declaredVariables);
    }

    private void createStaticBlock(List<DeclaredVariable> declaredVariables) {
        builder.addStaticBlock(CodeBlock.builder()
                .addStatement(String.format("java.util.Arrays.stream(%s.class.getDeclaredMethods()).filter(method -> method.getName().startsWith(\"get\")).forEach(method -> {System.out.println(method.getReturnType().getSimpleName());})", CLASSNAME))
                .build());
    }

    private void createVariable(DeclaredVariable declaredVariable) {
        if(declaredVariable.isRequired()) {
            builder.addMethod(RequiredMethod.from(declaredVariable));
        } else {
            builder.addMethod(OptionalMethod.from(declaredVariable));
        }
        variableCount++;
    }

    public int getVariableCount() {
        return variableCount;
    }
}
