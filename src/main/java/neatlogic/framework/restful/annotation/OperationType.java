package neatlogic.framework.restful.annotation;

import neatlogic.framework.restful.constvalue.OperationTypeEnum;

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
