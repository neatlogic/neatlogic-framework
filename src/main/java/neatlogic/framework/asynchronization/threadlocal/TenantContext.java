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
    private final String dataDbName = "";

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
            //this.setUseMasterDatabase(false);
            MDC.put("tenant", tenantUuid);
        }
        return this;
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
            // 使用master库
            //this.setUseMasterDatabase(true);
            List<String> tenantModuleGroupList = moduleMapper.getModuleGroupListByTenantUuid(tenantUuid);
            // 还原回租户库
            //this.setUseMasterDatabase(false);
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
        List<String> tenantModuleGroupList = searchModuleGroupList(this.tenantUuid);
        return ModuleUtil.getTenantActiveModuleList(tenantModuleGroupList);
    }

    public List<ModuleGroupVo> getActiveModuleGroupList() {
        List<String> tenantModuleGroupList = searchModuleGroupList(this.tenantUuid);
        //补充framework模块
        ModuleUtil.getTenantActiveModuleList(tenantModuleGroupList);
        List<ModuleGroupVo> activeModuleGroupList = new ArrayList<>();
        for (String group : tenantModuleGroupList) {
            ModuleGroupVo groupVo = ModuleUtil.getModuleGroup(group);
            if (groupVo != null) {
                activeModuleGroupList.add(groupVo);
            }
        }
        return activeModuleGroupList;
    }

    public Map<String, ModuleVo> getActiveModuleMap() {
        List<String> tenantModuleGroupList = searchModuleGroupList(this.tenantUuid);
        List<ModuleVo> activeModuleList = ModuleUtil.getTenantActiveModuleList(tenantModuleGroupList);
        Map<String, ModuleVo> activeModuleMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(activeModuleList)) {
            for (ModuleVo module : activeModuleList) {
                activeModuleMap.put(module.getId(), module);
            }
        }
        return activeModuleMap;
    }

    public boolean containsModule(String moduleId) {
        return getActiveModuleMap().containsKey(moduleId);
    }
}
