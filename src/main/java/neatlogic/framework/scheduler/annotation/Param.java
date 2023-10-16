package neatlogic.framework.scheduler.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
    String name() default "";
    String dataType() default "";
    String controlType() default "text";
    String controlValue() default "";
    String description() default "";
    boolean required() default true;
    int sort() default  0;
    String help() default "";
}
