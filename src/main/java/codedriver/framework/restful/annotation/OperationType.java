package codedriver.framework.restful.annotation;

import codedriver.framework.restful.core.constvalue.OperationTypeEnum;

import java.lang.annotation.*;

/**
 * 用来标识API类型的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationType {

	OperationTypeEnum type() default OperationTypeEnum.SEARCH;
}
