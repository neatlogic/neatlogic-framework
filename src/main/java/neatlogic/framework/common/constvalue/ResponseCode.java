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
