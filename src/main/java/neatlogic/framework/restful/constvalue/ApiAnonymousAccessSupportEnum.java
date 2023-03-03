/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

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
