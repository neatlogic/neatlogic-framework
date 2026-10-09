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

package neatlogic.framework.auth.core;

import neatlogic.framework.common.constvalue.systemuser.ISystemUser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Collections;
import java.util.Objects;

/** 权限定义底座，模块自行声明包含关系与系统用户默认授权，不承载页面授权存储。 */
public abstract class AuthBase {

    private String authModule;

    /**
     * 声明当前权限的系统用户出厂授权；由代码维护，不写入页面授权记录。
     * 未声明时不向任何系统用户授予当前权限。
     */
    public List<ISystemUser> getDefaultSystemUserList() {
        return Collections.emptyList();
    }

    public final String getAuthName() {
        return this.getClass().getSimpleName();
    }

    public abstract String getAuthDisplayName();

    public abstract String getAuthIntroduction();

    public abstract String getAuthGroup();

    public String getAuthModule() {
        if(StringUtils.isBlank(authModule)) {
            String moduleName = null;
            String packageName = this.getClass().getName();
            String[] packages = packageName.split("\\.");
            for (String p : packages) {
                if (Objects.equals(p, "auth")) {
                    authModule = moduleName;
                    break;
                }
                moduleName = p;
            }
        }
        return authModule;
    }

    public abstract Integer getSort();

    public boolean checkInvalid() {
        return true;
    }

    /**
     * 是否在前端页面展示，目前用于跨模块调接口权限授权，即A include B,B无需授权
     */
    public boolean isShow() {
        return true;
    }

    /**
     * 标记 用于标识base
     *
     * @return true|false
     */
    public boolean getIsDefault() {
        return false;
    }

    public List<Class<? extends AuthBase>> getIncludeAuths() {
        return (List<Class<? extends AuthBase>>) CollectionUtils.EMPTY_COLLECTION;
    }
}
