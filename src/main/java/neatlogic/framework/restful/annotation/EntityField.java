package neatlogic.framework.restful.annotation;

import neatlogic.framework.common.constvalue.ApiParamType;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EntityField {

    String name() default "";

    ApiParamType type() default ApiParamType.STRING;

    Class<?> member() default NotDefined.class;// 值成员

    String help() default "";

}
