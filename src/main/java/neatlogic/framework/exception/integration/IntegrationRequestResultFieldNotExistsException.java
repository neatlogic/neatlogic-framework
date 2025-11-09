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

import java.util.List;

/**
 * @author linbq
 * @since 2021/11/23 12:12
 **/
public class IntegrationRequestResultFieldNotExistsException extends ApiRuntimeException {

    private static final long serialVersionUID = 1161502312345475176L;

    public IntegrationRequestResultFieldNotExistsException(String field) {
        super("集成请求结果中：“{0}”字段不存在", field);
    }

    public IntegrationRequestResultFieldNotExistsException(List<String> fieldList) {
        super("集成请求结果中：'" + String.join("、", fieldList) + "'字段不存在", String.join("、", fieldList));
    }
}
