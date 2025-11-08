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

import org.reflections.Reflections;

import java.util.*;

public class TenantConfigFactory {

    private static final Map<String, ITenantConfig> tenantConfigMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("neatlogic");
        Set<Class<? extends ITenantConfig>> clazzSet = reflections.getSubTypesOf(ITenantConfig.class);

        for (Class<? extends ITenantConfig> clazz : clazzSet) {
            ITenantConfig[] tenantConfigs = clazz.getEnumConstants();
            if (tenantConfigs != null) {
                for (ITenantConfig tenantConfig : tenantConfigs) {
                    tenantConfigMap.put(tenantConfig.getKey(), tenantConfig);
                }
            }
        }
    }

    public static List<ITenantConfig> getTenantConfigList() {
        List<ITenantConfig> tenantConfigList = new ArrayList<>(tenantConfigMap.values());
        tenantConfigList.sort((e1, e2) -> e1.getKey().compareTo(e2.getKey()));
        return tenantConfigList;
    }

    public static ITenantConfig getTenantConfigByKey(String key) {
        return tenantConfigMap.get(key);
    }
}
