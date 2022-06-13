/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.asynchronization.threadlocal;

import codedriver.framework.common.RootConfiguration;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dao.mapper.LicenseMapper;
import codedriver.framework.dao.mapper.ModuleMapper;
import codedriver.framework.dto.ModuleGroupVo;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.dto.license.LicenseVo;
import codedriver.framework.exception.core.LicenseInvalidException;
import codedriver.framework.license.LicenseManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootConfiguration
public class TenantContext implements Serializable {
    private static final long serialVersionUID = -5977938340288247600L;
    private static final ThreadLocal<TenantContext> instance = new ThreadLocal<>();
    private String tenantUuid;
    private Boolean useDefaultDatasource = false;
    private List<ModuleVo> activeModuleList;
    private List<ModuleGroupVo> activeModuleGroupList;
    private Map<String, ModuleVo> activeModuleMap;
    private Boolean isData = false;
    private final String dataDbName = "";
    private LicenseVo licenseVo;

    private static ModuleMapper moduleMapper;
    private static LicenseMapper licenseMapper;

    @Autowired
    public void setModuleMapper(ModuleMapper _moduleMapper) {
        moduleMapper = _moduleMapper;
    }

    @Autowired
    public void setLicenseMapper(LicenseMapper _licenseMapper) {
        licenseMapper = _licenseMapper;
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
            context.setActiveModuleList(_tenantContext.getActiveModuleList());
            context.setLicenseVo(_tenantContext.getLicenseVo());
        }
        instance.set(context);
        return context;
    }

    public static TenantContext init(String _tenantUuid) {
        TenantContext context = new TenantContext(_tenantUuid);
        instance.set(context);
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
        return "codedriver_" + tenantUuid + "_data";
    }

    public String getDbName() {
        return "codedriver_" + tenantUuid;
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
            // 使用master库
            this.setUseDefaultDatasource(true);
            //防止 ArrayList HashMap 对象在存入 ehcache 之前迭代序列化时，另一个线程对这个 list、map 进行了修改操作
            List<String> tenantModuleGroupList = new ArrayList<>(moduleMapper.getModuleGroupListByTenantUuid(tenantUuid));
            this.activeModuleList = ModuleUtil.getTenantActiveModuleList(tenantModuleGroupList);
            this.activeModuleGroupList = new ArrayList<>();
            for (String group : tenantModuleGroupList) {
                ModuleGroupVo groupVo = ModuleUtil.getModuleGroup(group);
                if (groupVo != null) {
                    this.activeModuleGroupList.add(groupVo);
                }
            }
            // 查询对应租户的license
            this.licenseVo = LicenseManager.tenantLicenseMap.get(tenantUuid);
            if(licenseVo == null){
                throw new LicenseInvalidException(tenantUuid);
            }
            // 还原回租户库
            this.setUseDefaultDatasource(false);
            activeModuleMap = new HashMap<>();
            if (activeModuleList != null && activeModuleList.size() > 0) {
                for (ModuleVo module : activeModuleList) {
                    activeModuleMap.put(module.getId(), module);
                }
            }
        }
        return this;
    }

    public static TenantContext get() {
        return instance.get();
    }

    public void release() {
        instance.remove();
    }

    public Boolean getUseDefaultDatasource() {
        return useDefaultDatasource;
    }

    public void setUseDefaultDatasource(Boolean useDefaultDatasource) {
        this.useDefaultDatasource = useDefaultDatasource;
    }

    public List<ModuleVo> getActiveModuleList() {
        return activeModuleList;
    }

    public List<ModuleGroupVo> getActiveModuleGroupList() {
        return activeModuleGroupList;
    }

    public void setActiveModuleList(List<ModuleVo> activeModuleList) {
        this.activeModuleList = activeModuleList;
        activeModuleMap = new HashMap<>();
        if (activeModuleList != null && activeModuleList.size() > 0) {
            for (ModuleVo module : activeModuleList) {
                activeModuleMap.put(module.getId(), module);
            }
        }
    }

    public Map<String, ModuleVo> getActiveModuleMap() {
        return activeModuleMap;
    }

    public boolean containsModule(String moduleId) {
        return activeModuleMap.containsKey(moduleId);
    }

    public LicenseVo getLicenseVo() {
        return licenseVo;
    }

    public void setLicenseVo(LicenseVo licenseVo) {
        this.licenseVo = licenseVo;
    }
}
