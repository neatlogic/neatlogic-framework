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
