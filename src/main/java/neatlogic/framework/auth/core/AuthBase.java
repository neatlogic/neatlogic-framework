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

package neatlogic.framework.auth.core;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public abstract class AuthBase {
    public final String getAuthName() {
        return this.getClass().getSimpleName();
    }

    public abstract String getAuthDisplayName();

    public abstract String getAuthIntroduction();

    public abstract String getAuthGroup();

    public abstract Integer getSort();

    public boolean checkInvalid() {
        return true;
    }

    ;

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
