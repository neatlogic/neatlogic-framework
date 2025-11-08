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

package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class LicenseExpiredException extends ApiRuntimeException {
    public LicenseExpiredException(String moduleId) {
        super("当前许可缺少访问模块 {0} 的授权，尝试访问会被拒绝。原因是许可过期，请联系厂商更换，或者避免访问该模块下的任何功能。", moduleId);
    }

}
