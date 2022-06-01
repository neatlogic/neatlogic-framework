/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.annotation;

import codedriver.framework.common.constvalue.ApiParamType;

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

    String defaultValue() default "";

    Class<?> explode() default NotDefined.class;

    boolean xss() default false;

    int maxLength() default -1;

    int minLength() default -1;

}
