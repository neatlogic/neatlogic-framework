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
            if (tenantConfigs != null && tenantConfigs.length > 0) {
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
