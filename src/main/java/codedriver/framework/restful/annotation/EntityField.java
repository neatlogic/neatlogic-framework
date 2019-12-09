package codedriver.framework.restful.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import codedriver.framework.apiparam.core.ApiParamType;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EntityField {

	String name() default "";

	ApiParamType type() default ApiParamType.STRING;

}
