package nl.benkhard.envio.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(EnvironmentVariables.class)
public @interface EnvironmentVariable {
    String name();
    String description() default "";
    String defaultValue() default "";
    boolean required() default false;
}


