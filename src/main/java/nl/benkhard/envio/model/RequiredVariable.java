package nl.benkhard.envio.model;

import javax.lang.model.element.Element;

public class RequiredVariable extends DeclaredVariable {

    public RequiredVariable(String name, String description, Element element) {
        super(true, description, null, name, element);
    }
}
