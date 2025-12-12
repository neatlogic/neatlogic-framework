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

package neatlogic.framework.exception.server;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ServerHostIsBankException extends ApiRuntimeException {

    public ServerHostIsBankException(Integer serverId) {
        super("serverId为{0}的应用服务器的服务器地址为空，请到系统配置->服务节点状态页面设置", serverId);
    }
}
