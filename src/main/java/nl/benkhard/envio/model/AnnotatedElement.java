package nl.benkhard.envio.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@Data
@AllArgsConstructor
public class AnnotatedElement {
    private TypeElement annotation;
    private Element element;
}
