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

package neatlogic.framework.asynchronization.threadlocal;

import neatlogic.framework.common.RootConfiguration;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dao.mapper.ModuleMapper;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.dto.module.ModuleVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RootConfiguration
public class TenantContext implements Serializable {
    private static final long serialVersionUID = -5977938340288247600L;
    private static final ThreadLocal<TenantContext> instance = new ThreadLocal<>();
    private static final ConcurrentHashMap<String, List<String>> tenantModuleGroupListMap = new ConcurrentHashMap<>();
    private String tenantUuid;
    private Boolean useDefaultDatasource = false;
    private Boolean isData = false;

    private static ModuleMapper moduleMapper;

    @Autowired
    public void setModuleMapper(ModuleMapper _moduleMapper) {
        moduleMapper = _moduleMapper;
    }

    public static TenantContext init() {
        TenantContext context = new TenantContext();
        instance.set(context);
        return context;
    }

    public static TenantContext init(TenantContext _tenantContext) {
        TenantContext context = new TenantContext();
        if (_tenantContext != null) {
            context.setTenantUuid(_tenantContext.getTenantUuid());
            MDC.put("tenant", _tenantContext.getTenantUuid());
        }
        instance.set(context);
        return context;
    }

    public static TenantContext init(String _tenantUuid) {
        TenantContext context = new TenantContext(_tenantUuid);
        instance.set(context);
        MDC.put("tenant", _tenantUuid);
        return context;
    }

    private TenantContext() {

    }

    private TenantContext(String _tenantUuid) {
        this.tenantUuid = _tenantUuid;
    }

    public String getTenantUuid() {
        if (useDefaultDatasource) {
            return null;
        } else {
            return tenantUuid;
        }
    }

    public String getDataDbName() {
        return "neatlogic_" + tenantUuid + "_data";
    }

    public String getDbName() {
        return "neatlogic_" + tenantUuid;
    }

    private TenantContext setTenantUuid(String tenantUuid) {
        this.tenantUuid = tenantUuid;
        return this;
    }

    public void switchDataDatabase() {
        this.isData = true;
    }

    public void switchDefaultDatabase() {
        this.isData = false;
    }

    public Boolean isData() {
        return this.isData;
    }

    public TenantContext switchTenant(String tenantUuid) {
        if (StringUtils.isNotBlank(tenantUuid)) {
            this.tenantUuid = tenantUuid;
            MDC.put("tenant", tenantUuid);
        }
        return this;
    }

    /**
     * 清理指定租户的模块组缓存。
     * 租户禁用、删除、重新启用时需要清掉旧模块视图。
     *
     * @param tenantUuid 租户uuid
     */
    public static void removeTenantCache(String tenantUuid) {
        if (StringUtils.isNotBlank(tenantUuid)) {
            tenantModuleGroupListMap.remove(tenantUuid);
        }
    }

    public static TenantContext get() {
        if (instance.get() == null) {
            init();
        }
        return instance.get();
    }

    private List<String> searchModuleGroupList(String tenantUuid) {
        List<String> moduleGroupList = tenantModuleGroupListMap.get(tenantUuid);
        if (moduleGroupList == null) {
            List<String> tenantModuleGroupList = moduleMapper.getModuleGroupListByTenantUuid(tenantUuid);
            tenantModuleGroupListMap.put(tenantUuid, tenantModuleGroupList);
        }
        return tenantModuleGroupListMap.get(tenantUuid);
    }

    public void release() {
        MDC.remove("tenant");
        instance.remove();
    }

    public Boolean getUseDefaultDatasource() {
        return useDefaultDatasource;
    }

    /**
     * 切换数据库
     * 注意：不能在事务场景使用此方法，否则会切库失败
     *
     * @param useDefaultDatasource true 使用neatlogic 库 ，false 还原使用租户库
     */
    public void setUseMasterDatabase(Boolean useDefaultDatasource) {
        this.useDefaultDatasource = useDefaultDatasource;
    }

    public List<ModuleVo> getActiveModuleList() {
        if (StringUtils.isNotBlank(this.tenantUuid)) {
            List<String> tenantModuleGroupList = searchModuleGroupList(this.tenantUuid);
            return ModuleUtil.getTenantActiveModuleList(tenantModuleGroupList);
        }
        return new ArrayList<>();
    }

    public List<ModuleGroupVo> getActiveModuleGroupList() {
        List<ModuleGroupVo> activeModuleGroupList = new ArrayList<>();
        if (StringUtils.isNotBlank(this.tenantUuid)) {
            List<String> tenantModuleGroupList = searchModuleGroupList(this.tenantUuid);
            //补充framework模块
            ModuleUtil.getTenantActiveModuleList(tenantModuleGroupList);
            for (String group : tenantModuleGroupList) {
                ModuleGroupVo groupVo = ModuleUtil.getModuleGroup(group);
                if (groupVo != null) {
                    activeModuleGroupList.add(groupVo);
                }
            }
        }
        return activeModuleGroupList;
    }

    public Map<String, ModuleVo> getActiveModuleMap() {
        Map<String, ModuleVo> activeModuleMap = new HashMap<>();
        if (StringUtils.isNotBlank(this.tenantUuid)) {
            List<String> tenantModuleGroupList = searchModuleGroupList(this.tenantUuid);
            List<ModuleVo> activeModuleList = ModuleUtil.getTenantActiveModuleList(tenantModuleGroupList);
            if (CollectionUtils.isNotEmpty(activeModuleList)) {
                for (ModuleVo module : activeModuleList) {
                    activeModuleMap.put(module.getId(), module);
                }
            }
        }
        return activeModuleMap;
    }

    public boolean containsModule(String moduleId) {
        return getActiveModuleMap().containsKey(moduleId);
    }
}
