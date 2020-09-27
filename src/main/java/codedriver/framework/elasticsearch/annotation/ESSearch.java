package codedriver.framework.elasticsearch.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import codedriver.framework.restful.annotation.NotDefined;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ESSearch {
	String type() default "";
	Class<?> paramType() default NotDefined.class;
}
