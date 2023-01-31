/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.fulltextindex.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.bootstrap.CodedriverWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.exception.fulltextindex.FullTextIndexTypeNotFoundException;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexTypeVo;

import java.util.*;
import java.util.stream.Collectors;

@RootComponent
public class FullTextIndexHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IFullTextIndexHandler> componentMap = new HashMap<>();
    private static final List<FullTextIndexTypeVo> fullTextIndexTypeList = new ArrayList<>();


    public static IFullTextIndexHandler getHandler(String type) {
        if (!componentMap.containsKey(type) || componentMap.get(type) == null) {
            throw new RuntimeException("找不到类型为：" + type + "的全文索引组件");
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
            throw new RuntimeException("找不到类型为：" + type.getTypeName() + "的全文索引组件");
        }
        return componentMap.get(type.getType());
    }


    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IFullTextIndexHandler> myMap = context.getBeansOfType(IFullTextIndexHandler.class);
        for (Map.Entry<String, IFullTextIndexHandler> entry : myMap.entrySet()) {
            IFullTextIndexHandler component = entry.getValue();
            if (component.getType() != null) {
                componentMap.put(component.getType().getType(), component);
                fullTextIndexTypeList.add(new FullTextIndexTypeVo(context.getModuleId(), component.getType().getType(), component.getType().getTypeName(), component.getType().isActiveGlobalSearch()));
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
