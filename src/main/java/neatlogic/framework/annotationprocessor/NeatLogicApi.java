package neatlogic.framework.annotationprocessor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface NeatLogicApi {
    String value() default "";
}
