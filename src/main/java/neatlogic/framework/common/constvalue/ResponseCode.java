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

package neatlogic.framework.common.constvalue;

import neatlogic.framework.exception.auth.AuthFailedException;
import neatlogic.framework.exception.auth.AuthTypeNotFoundException;
import neatlogic.framework.exception.core.ApiFieldValidException;
import neatlogic.framework.exception.core.ApiRateLimiterException;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.core.NotFoundEditTargetException;
import neatlogic.framework.exception.login.LoginExpiredException;
import neatlogic.framework.exception.resubmit.ResubmitException;
import neatlogic.framework.exception.tenant.TenantNotFoundException;
import neatlogic.framework.exception.type.LicenseInvalidException;
import neatlogic.framework.exception.type.PermissionDeniedException;


public enum ResponseCode {
    RATE_LIMITER_TOKEN_BUCKET(429, "系统繁忙", ApiRateLimiterException.class),
    EXCEPTION(500, "非已知异常", Exception.class),
    API_RUNTIME(520, "运行已知异常", ApiRuntimeException.class),
    TENANT_NOTFOUND(521, "租户不存在", TenantNotFoundException.class),
    AUTH_FAILED(522, "认证类型失败", AuthFailedException.class),
    PERMISSION_DENIED(523, "没有资源权限", PermissionDeniedException.class),
    RESUBMIT(524, "重复提交请求", ResubmitException.class),
    AUTH_TYPE_NOTFOUND(525, "认证类型不存在", AuthTypeNotFoundException.class),
    EDIT_TARGET_NOTFOUND(526, "对象不存在", NotFoundEditTargetException.class),
    LOGIN_EXPIRED(527, "登录会话超时或已终止", LoginExpiredException.class),
    API_FIELD_INVALID(530, "入参校验失败", ApiFieldValidException.class),
    LICENSE_INVALID(550, "license认证失败", LicenseInvalidException.class);


    private final Integer code;
    private final String description;
    private final Class<?> ex;

    ResponseCode(Integer code, String description, Class<?> ex) {
        this.code = code;
        this.description = description;
        this.ex = ex;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Class<?> getEx() {
        return ex;
    }

    public String getMessage(Object... args) throws Exception {
        if (args != null && args.length > 0) {
            return ((ApiRuntimeException) ex.getConstructor(String.class).newInstance(args)).getMessage();
        } else {
            return ((ApiRuntimeException) ex.newInstance()).getMessage();
        }
    }
}
