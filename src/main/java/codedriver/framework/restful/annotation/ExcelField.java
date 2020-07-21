package codedriver.framework.restful.annotation;

import codedriver.framework.common.constvalue.ApiParamType;

import java.lang.annotation.*;

/**
 * 用来标识VO中的字段是否作为导出Excel列的注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelField {

	String name() default "";

	ApiParamType type() default ApiParamType.STRING;

}
