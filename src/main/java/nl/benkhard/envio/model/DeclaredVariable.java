package nl.benkhard.envio.model;

import lombok.Data;
import nl.benkhard.envio.annotation.EnvironmentVariable;

import javax.lang.model.element.Element;

@Data
public abstract class DeclaredVariable {
    private boolean required;
    private String description;
    private String defaultValue;
    private String name;
    private Element element;

    public DeclaredVariable(boolean required, String description, String defaultValue, String name, Element element) {
        this.required = required;
        this.defaultValue = defaultValue;
        this.name = name;
        this.element = element;
    }

    public static DeclaredVariable from(EnvironmentVariable variable, Element element) {
        if(variable.required()) {
            return new RequiredVariable(variable.name(), variable.description(), element);
        } else {
            return new OptionalVariable(variable.name(), variable.description(), variable.defaultValue(), element);
        }
    }
}
