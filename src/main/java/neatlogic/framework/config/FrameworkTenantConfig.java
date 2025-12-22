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

package neatlogic.framework.config;

import neatlogic.framework.util.$;

public enum FrameworkTenantConfig implements ITenantConfig{
    API_QPS("apiqps", null, "nfc.frameworktenantconfig.apiqps"),
    DISABLED_MODULEGROUPLIST("diabled.modulegrouplist", null,"nfc.frameworktenantconfig.disabledmodulegrouplist"),
    MOBILE_FILE_DOWNLOAD_ENABLED("mobile.file.download.enabled", "0","nfc.frameworktenantconfig.mobilefiledownloadenabled"),
    LOGIN_LOCKED_FAILED_COUNT("login.locked.failed.count", "5","nfc.frameworktenantconfig.loginlockedfailedcount"),
    LOGIN_LOCKED_TIME("login.locked.times.limit", "10","nfc.frameworktenantconfig.loginlockedtime"),
    LOGIN_NEED_LOCK("login.need.lock", "0","nfc.frameworktenantconfig.loginneedlock"),
    ;

    String key;
    String value;
    String description;

    FrameworkTenantConfig(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return $.t(description);
    }
}
