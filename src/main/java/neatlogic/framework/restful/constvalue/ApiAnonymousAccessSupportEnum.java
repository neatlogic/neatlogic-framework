/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.restful.constvalue;

/**
 * 接口是否支持匿名访问
 */
public enum ApiAnonymousAccessSupportEnum {
    ANONYMOUS_ACCESS_FORBIDDEN(false, false), // 不支持匿名访问
    ANONYMOUS_ACCESS_WITH_ENCRYPTION(true, true), // 支持匿名访问但token需要加密
    ANONYMOUS_ACCESS_WITHOUT_ENCRYPTION(true, false); // 支持匿名访问且token无需加密

    final private boolean isSupportAnonymousAccess; // 是否支持匿名访问
    final private boolean isRequireTokenEncryption; // token是否需要加密

    public boolean isSupportAnonymousAccess() {
        return isSupportAnonymousAccess;
    }

    public boolean isRequireTokenEncryption() {
        return isRequireTokenEncryption;
    }

    ApiAnonymousAccessSupportEnum(boolean isSupportAnonymousAccess, boolean isRequireTokenEncryption) {
        this.isSupportAnonymousAccess = isSupportAnonymousAccess;
        this.isRequireTokenEncryption = isRequireTokenEncryption;
    }
}
