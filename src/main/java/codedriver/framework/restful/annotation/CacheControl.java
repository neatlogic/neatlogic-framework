package codedriver.framework.restful.annotation;

import codedriver.framework.common.constvalue.CacheControlType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheControl {
	CacheControlType cacheControlType();
	int maxAge();
}
