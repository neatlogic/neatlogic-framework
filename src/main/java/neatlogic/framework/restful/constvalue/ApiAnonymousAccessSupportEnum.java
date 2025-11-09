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

package neatlogic.framework.restful.constvalue;

/**
 * 接口是否支持匿名访问
 */
public enum ApiAnonymousAccessSupportEnum {
    ANONYMOUS_ACCESS_FORBIDDEN(false, false), // 不支持匿名访问
    ANONYMOUS_ACCESS_WITH_ENCRYPTION(true, true), // 支持匿名访问但token需要加密
    ANONYMOUS_ACCESS_WITHOUT_ENCRYPTION(true, false); // 支持匿名访问且token无需加密

    private final boolean isSupportAnonymousAccess; // 是否支持匿名访问
    private final boolean isRequireTokenEncryption; // token是否需要加密

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
