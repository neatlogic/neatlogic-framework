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

public class IntegrationRateLimitExceededException extends ApiRuntimeException {

    private static final long serialVersionUID = -1230915172406780380L;

    public IntegrationRateLimitExceededException(String name, Integer interval, Integer count) {
        super("集成“{0}”在{1}秒内最多允许调用{2}次，请稍后再试", name, interval, count);
    }
}
