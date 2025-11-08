/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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

    boolean isExcluded() default false;

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
