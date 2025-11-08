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

package neatlogic.framework.exception.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.$;

public class ApiRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 9206337410118158624L;
    private String errorCode;

    private JSONObject param;

    public ApiRuntimeException() {
        super();
    }

    public ApiRuntimeException(Throwable ex) {
        super(ex);
    }

    public ApiRuntimeException(String message, Throwable ex) {
        super($.t(message), ex);
    }

    public ApiRuntimeException(String message, Throwable ex, Object... values) {
        super($.t(message, values), ex);
    }

    public ApiRuntimeException(String key) {
        super($.t(key));
    }

    public ApiRuntimeException(String key, Object... values) {
        super($.t(key, values));
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public JSONObject getParam() {
        return param;
    }

}
