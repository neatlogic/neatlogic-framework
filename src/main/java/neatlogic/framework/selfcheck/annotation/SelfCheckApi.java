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

package neatlogic.framework.selfcheck.annotation;

import neatlogic.framework.selfcheck.constvalue.SelfCheckRisk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 显式声明 API 可用于功能自检。
 * 未标记的 API 不得出现在自检目标列表，也不得通过自检执行器调用。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelfCheckApi {
    /** API 的数据变更风险。 */
    SelfCheckRisk risk() default SelfCheckRisk.READ_ONLY;

    /** 选择 API 时建议使用的默认验真脚本。 */
    String defaultScript() default "";

    /** 请求参数中需要脱敏的 JSON 路径。 */
    String[] sensitiveParamPathList() default {};

    /** 返回结果中需要脱敏的 JSON 路径。 */
    String[] sensitiveResultPathList() default {};
}
