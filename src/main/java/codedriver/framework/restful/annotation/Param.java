package codedriver.framework.restful.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import codedriver.framework.common.apiparam.ApiParamBase;
import codedriver.framework.common.apiparam.ApiParamType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

	String name() default "";

	ApiParamType type() default ApiParamType.STRING;

	boolean isRequired() default false;

	String desc() default "";

}
