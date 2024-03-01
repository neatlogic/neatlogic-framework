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

package neatlogic.framework.fulltextindex.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.exception.fulltextindex.FullTextIndexComponentOfTypeNotFoundException;
import neatlogic.framework.exception.fulltextindex.FullTextIndexTypeNotFoundException;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexTypeVo;

import java.util.*;
import java.util.stream.Collectors;

@RootComponent
public class FullTextIndexHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IFullTextIndexHandler> componentMap = new HashMap<>();
    private static final List<FullTextIndexTypeVo> fullTextIndexTypeList = new ArrayList<>();
    private static final Map<String, IFullTextIndexType> fullTextIndexTypeMap = new HashMap<>();


    public static IFullTextIndexHandler getHandler(String type) {
        if (!componentMap.containsKey(type) || componentMap.get(type) == null) {
            throw new FullTextIndexComponentOfTypeNotFoundException(type);
        }
        return componentMap.get(type);
    }

    /**
     * 返回当前租户拥有索引的模块列表
     *
     * @return 模块id列表
     */
    public static List<String> getModuleIdList() {
        TenantContext tenantContext = TenantContext.get();
        List<ModuleVo> moduleList = tenantContext.getActiveModuleList();
        List<String> moduleIdList = new ArrayList<>();
        for (ModuleVo moduleVo : moduleList) {
            if (fullTextIndexTypeList.stream().anyMatch(type -> type.getModuleId().equals(moduleVo.getId()))) {
                moduleIdList.add(moduleVo.getId());
            }
        }
        return moduleIdList;
    }

    /**
     * 返回当前租户的索引类型
     *
     * @return 索引类型数组
     */
    public static List<FullTextIndexTypeVo> getAllTypeList() {
        TenantContext tenantContext = TenantContext.get();
        List<ModuleVo> moduleList = tenantContext.getActiveModuleList();
        List<FullTextIndexTypeVo> returnTypeList = new ArrayList<>();
        for (ModuleVo moduleVo : moduleList) {
            returnTypeList.addAll(fullTextIndexTypeList.stream().filter(type -> type.getModuleId().equals(moduleVo.getId())).collect(Collectors.toList()));
        }
        for (FullTextIndexTypeVo typeVo : returnTypeList) {
            IFullTextIndexType fullTextIndexType = fullTextIndexTypeMap.get(typeVo.getType());
            if (fullTextIndexType != null) {
                typeVo.setTypeName(fullTextIndexType.getTypeName());
            }
        }
        return returnTypeList;
    }

    /**
     * 根据名字返回类型信息
     *
     * @param type 类型名称
     * @return 类型对象
     */
    public static FullTextIndexTypeVo getTypeByName(String type) {
        Optional<FullTextIndexTypeVo> op = fullTextIndexTypeList.stream().filter(d -> d.getType().equals(type)).findFirst();
        if (op.isPresent()) {
            return op.get();
        } else {
            throw new FullTextIndexTypeNotFoundException(type);
        }
    }

    public static IFullTextIndexHandler getHandler(IFullTextIndexType type) {
        if (!componentMap.containsKey(type.getType()) || componentMap.get(type.getType()) == null) {
            throw new FullTextIndexComponentOfTypeNotFoundException(type.getTypeName());
        }
        return componentMap.get(type.getType());
    }


    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IFullTextIndexHandler> myMap = context.getBeansOfType(IFullTextIndexHandler.class);
        for (Map.Entry<String, IFullTextIndexHandler> entry : myMap.entrySet()) {
            IFullTextIndexHandler component = entry.getValue();
            if (component.getType() != null) {
                componentMap.put(component.getType().getType(), component);
                fullTextIndexTypeList.add(new FullTextIndexTypeVo(context.getModuleId(), component.getType().getType(), component.getType().getTypeName(), component.getType().isActiveGlobalSearch()));
                fullTextIndexTypeMap.put(component.getType().getType(), component.getType());
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
