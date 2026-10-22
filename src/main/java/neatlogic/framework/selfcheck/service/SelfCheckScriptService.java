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

package neatlogic.framework.selfcheck.service;

import neatlogic.framework.selfcheck.dto.SelfCheckScriptContextVo;
import neatlogic.framework.selfcheck.dto.SelfCheckScriptResultVo;

/** 自检专用 JavaScript 业务适配服务，不复用原有 JavascriptUtil。 */
public interface SelfCheckScriptService {
    /** 仅编译脚本体并返回语法诊断。 */
    SelfCheckScriptResultVo validate(String script);

    /** 在隔离上下文中执行脚本体，脚本必须直接返回 Boolean。 */
    SelfCheckScriptResultVo execute(String script, SelfCheckScriptContextVo contextVo, Integer timeoutSeconds);
}
