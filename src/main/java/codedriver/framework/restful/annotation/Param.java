package codedriver.framework.restful.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import codedriver.framework.apiparam.core.ApiParamType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

	String name() default "";

	ApiParamType type() default ApiParamType.STRING;

	boolean isRequired() default false;

	String rule() default "";

	String desc() default "";

	Class explode() default NotDefined.class;

	int length() default -1;
}
