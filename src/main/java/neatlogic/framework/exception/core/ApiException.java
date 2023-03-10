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

import neatlogic.framework.util.I18nUtils;

public class ApiException extends Exception {
    private static final long serialVersionUID = 9163793348080280240L;
    private String errorCode;

    private Object[] values;

    public Object[] getValues() {
        return values;
    }

    public ApiException() {
        super();
    }

    public ApiException(Throwable ex) {
        super(ex);
    }

    public ApiException(String message, Throwable ex) {
        super(I18nUtils.getMessage(message), ex);
    }

    public ApiException(String message) {
        super(I18nUtils.getMessage(message));
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public ApiException(String message, Object... values) {
        super(I18nUtils.getMessage(message,values));
    }

}
