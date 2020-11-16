package nl.benkhard.envio.model;

import javax.lang.model.element.Element;

public class OptionalVariable extends DeclaredVariable {

    public OptionalVariable(String name, String description, String defaultValue, Element element) {
        super(false, description, defaultValue, name, element);
    }
}
