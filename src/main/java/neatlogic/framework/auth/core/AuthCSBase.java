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

package neatlogic.framework.auth.core;

/** 商业权限标签，所属模块按标准包根目录确定，不受 cs 或子功能目录影响。 */
public abstract class AuthCSBase extends AuthBase {
    /** 识别标准框架及业务模块包；自定义包结构继续沿用父类规则。 */
    @Override
    public String getAuthModule() {
        String[] packages = getClass().getName().split("\\.");
        if (packages.length > 3 && "neatlogic".equals(packages[0])
                && ("module".equals(packages[1]) || "framework".equals(packages[1]))) {
            return packages[2];
        }
        return super.getAuthModule();
    }
}
