/*
 * Copyright(c) 2023 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.restful.annotation;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.IEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    String name() default "";

    ApiParamType type() default ApiParamType.STRING;

    boolean isRequired() default false;

    String rule() default "";

    String desc() default "";

    String help() default "";

    String defaultValue() default "";

    Class<?> explode() default NotDefined.class;

    boolean xss() default false;

    int maxLength() default -1;

    int minLength() default -1;

    int maxSize() default -1;

    int minSize() default -1;

    Class<? extends IEnum> member() default NotDefined.class;// 枚举类，引用枚举作为入参合法值时要求该枚举实现IEunm接口并重写getValue方法
}
