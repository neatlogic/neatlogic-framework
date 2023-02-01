/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.exception.core;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import com.alibaba.fastjson.JSONObject;

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
        super(message, ex);
    }

    public ApiRuntimeException(String message) {
        super(message);
    }

    public ApiRuntimeException(String message, JSONObject param) {
        super(message);
        this.param = param;
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
