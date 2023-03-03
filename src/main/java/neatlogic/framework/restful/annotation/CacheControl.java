package neatlogic.framework.restful.annotation;

import neatlogic.framework.common.constvalue.CacheControlType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheControl {
	CacheControlType cacheControlType();
	int maxAge();
}
