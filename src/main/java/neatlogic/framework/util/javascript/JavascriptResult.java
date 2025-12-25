/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the Sustainable Use License (SUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.util.javascript;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.exception.core.ApiRuntimeException;

public class JavascriptResult {
    private boolean result = false;
    @JSONField(serialize = false)
    private ApiRuntimeException error = null;

    private String errorMsg = null;


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrorMsg() {
        if (error != null) {
            errorMsg = error.getMessage();
        }
        return errorMsg;
    }


    public ApiRuntimeException getError() {
        return error;
    }

    public void setError(ApiRuntimeException error) {
        this.error = error;
    }
}
