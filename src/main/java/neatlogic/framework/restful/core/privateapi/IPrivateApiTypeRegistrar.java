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

package neatlogic.framework.restful.core.privateapi;

import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;

/**
 * private API 类型注册器。
 * 每种接口类型各自实现自己的扫描与注册逻辑，factory 只负责统一调度。
 */
public interface IPrivateApiTypeRegistrar {

    /**
     * 返回注册顺序，用于保持各类型注册的稳定性。
     */
    int getOrder();

    /**
     * 在指定模块上下文中注册当前类型的 private API。
     */
    void register(NeatLogicWebApplicationContext context);
}
