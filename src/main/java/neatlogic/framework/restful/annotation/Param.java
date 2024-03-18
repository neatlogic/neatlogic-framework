/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
