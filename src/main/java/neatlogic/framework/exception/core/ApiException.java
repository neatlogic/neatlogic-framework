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

import neatlogic.framework.util.$;

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
        super($.t(message), ex);
    }

    public ApiException(String message) {
        super($.t(message));
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public ApiException(String message, Object... values) {
        super($.t(message,values));
    }

}
