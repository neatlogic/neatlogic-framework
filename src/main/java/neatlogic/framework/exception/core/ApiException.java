/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
