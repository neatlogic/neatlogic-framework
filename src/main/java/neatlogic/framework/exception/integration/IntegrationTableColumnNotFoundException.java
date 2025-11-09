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

package neatlogic.framework.exception.integration;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class IntegrationTableColumnNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1061691151919475176L;

    public IntegrationTableColumnNotFoundException(String integration, String columnUuid) {
        super("在{0}集成配置输出转换theadList列表中找不到：“{1}”列信息", integration, columnUuid);
    }

}
