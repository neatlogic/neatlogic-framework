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
import neatlogic.framework.exception.user.UserPasswordExpiredException;


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
    PASSWORD_EXPIRED(533, "密码过期", UserPasswordExpiredException.class),
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
        if(ApiRuntimeException.class.isAssignableFrom(ex)) {
            if (args != null && args.length > 0) {
                return ((ApiRuntimeException) ex.getConstructor(String.class).newInstance(args)).getMessage();
            } else {
                return ((ApiRuntimeException) ex.newInstance()).getMessage();
            }
        }else{
            return args[0].toString();
        }
    }
}
