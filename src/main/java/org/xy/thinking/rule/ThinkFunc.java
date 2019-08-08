package org.xy.thinking.rule;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Target(value=ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThinkFunc {
    String domain() default "eval";
    String name() default "default";
}
