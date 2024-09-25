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
