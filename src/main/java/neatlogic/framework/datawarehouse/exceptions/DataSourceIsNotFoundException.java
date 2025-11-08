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

package neatlogic.framework.datawarehouse.exceptions;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class DataSourceIsNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = -3381448125762128530L;

    public DataSourceIsNotFoundException(Long id) {
        super("数据源" + id + "不存在");
    }

    public DataSourceIsNotFoundException(String idListStr) {
        super("数据源：“" + idListStr + "”不存在");
    }
}
