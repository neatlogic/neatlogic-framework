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

package neatlogic.framework.lcs.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @author linbq
 * @since 2021/8/10 10:30
 **/
public class LineHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1669102356313895841L;

    public LineHandlerNotFoundException(String handler) {
        super("知识插件：“{0}”不存在", handler);
    }
}
