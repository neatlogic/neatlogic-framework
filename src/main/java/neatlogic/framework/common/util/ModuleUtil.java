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

package neatlogic.framework.common.util;

import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.dto.module.ModuleVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class ModuleUtil {
    static Logger logger = LoggerFactory.getLogger(ModuleUtil.class);
    private static final List<ModuleVo> moduleList = new ArrayList<>();
    private static final Map<String, ModuleVo> moduleMap = new HashMap<>();

    public static List<ModuleVo> getAllModuleList() {
        return moduleList;
    }

    public static List<ModuleGroupVo> getAllModuleGroupList() {
        List<ModuleGroupVo> moduleGroupList = new ArrayList<>();
        Set<String> groupSet = new HashSet<>();
        for (ModuleVo moduleVo : moduleList) {
            // master模块不允许用户添加
            if (!moduleVo.getId().equals("master")) {
                if (!groupSet.contains(moduleVo.getGroup())) {
                    if (StringUtils.isNotBlank(moduleVo.getGroup()) && StringUtils.isNotBlank(moduleVo.getGroupName())) {
                        ModuleGroupVo moduleGroup = new ModuleGroupVo();
                        moduleGroup.setGroup(moduleVo.getGroup());
                        moduleGroup.setGroupName(moduleVo.getGroupName());
                        moduleGroup.setGroupDescription(moduleVo.getGroupDescription());
                        moduleGroup.setGroupSort(moduleVo.getGroupSort());
                        moduleGroupList.add(moduleGroup);
                        groupSet.add(moduleVo.getGroup());
                    }
                }
            }
        }
        moduleGroupList.sort(Comparator.comparingInt(ModuleGroupVo::getGroupSort));
        return moduleGroupList;
    }

    public static ModuleVo getModuleById(String moduleId) {
        return moduleMap.get(moduleId);
    }

    public static void addModule(ModuleVo moduleVo) {
        moduleMap.put(moduleVo.getId(), moduleVo);
        moduleList.add(moduleVo);
    }

    public static void removeModule(ModuleVo moduleVo) {
        moduleMap.remove(moduleVo.getId());
        moduleList.removeIf(m -> m.getId().equals(moduleVo.getId()));
    }

    public static List<ModuleVo> getTenantActiveModuleList(List<String> tenantModuleGroupList) {
        List<ModuleVo> returnList = new ArrayList<>();
        if (!tenantModuleGroupList.contains("framework")) {
            tenantModuleGroupList.add("framework");
        }

        for (ModuleVo moduleVo : moduleList) {
            if (tenantModuleGroupList.contains(moduleVo.getGroup())) {
                returnList.add(moduleVo);
            }
        }

        return returnList;
    }

    public static ModuleGroupVo getModuleGroup(String group) {
        ModuleGroupVo moduleGroup = null;
        Set<Entry<String, ModuleVo>> moduleSet = moduleMap.entrySet();
        for (Entry<String, ModuleVo> moduleEntry : moduleSet) {
            ModuleVo moduleVo = moduleEntry.getValue();
            if (moduleVo.getGroup().equals(group)) {
                if (moduleGroup == null) {
                    moduleGroup = new ModuleGroupVo();
                    moduleGroup.setGroup(moduleVo.getGroup());
                    moduleGroup.setGroupName(moduleVo.getGroupName());
                    moduleGroup.setGroupDescription(moduleVo.getGroupDescription());
                    moduleGroup.setGroupSort(moduleVo.getGroupSort());

                    List<ModuleVo> moduleList = new ArrayList<ModuleVo>();
                    moduleList.add(moduleVo);
                    moduleGroup.setModuleList(moduleList);
                } else {
                    moduleGroup.getModuleList().add(moduleVo);
                }
            }
        }
        return moduleGroup;
    }

    public static Map<String, ModuleGroupVo> getModuleGroupMap() {
        Map<String, ModuleGroupVo> moduleGroupMap = new HashMap<String, ModuleGroupVo>();
        Set<Entry<String, ModuleVo>> moduleSet = moduleMap.entrySet();
        for (Entry<String, ModuleVo> moduleEntry : moduleSet) {
            ModuleVo moduleVo = moduleEntry.getValue();
            ModuleGroupVo moduleGroupVo = null;
            if (moduleGroupMap.containsKey(moduleVo.getGroup())) {
                moduleGroupVo = (ModuleGroupVo) moduleGroupMap.get(moduleVo.getGroup());
                moduleGroupVo.getModuleList().add(moduleVo);
            } else {
                moduleGroupVo = new ModuleGroupVo();
                moduleGroupVo.setGroup(moduleVo.getGroup());
                moduleGroupVo.setGroupName(moduleVo.getGroupName());
                moduleGroupVo.setGroupDescription(moduleVo.getGroupDescription());
                moduleGroupVo.setGroupSort(moduleVo.getGroupSort());
                List<ModuleVo> moduleList = new ArrayList<ModuleVo>();
                moduleList.add(moduleVo);
                moduleGroupVo.setModuleList(moduleList);
            }
            moduleGroupMap.put(moduleGroupVo.getGroup(), moduleGroupVo);

        }
        return moduleGroupMap;
    }
}
