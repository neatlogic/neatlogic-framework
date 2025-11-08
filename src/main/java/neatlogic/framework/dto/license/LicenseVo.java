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

package neatlogic.framework.dto.license;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LicenseVo implements Serializable {
    @EntityField(name = "nfd.licensevo.entityfield.name.dburl", type = ApiParamType.STRING)
    private String dbUrl;
    @EntityField(name = "nfd.licensevo.entityfield.name.purchaser", type = ApiParamType.STRING)
    private String purchaser;
    @EntityField(name = "common.createdate", type = ApiParamType.LONG)
    private Long createDate;
    @EntityField(name = "nfd.licensevo.entityfield.name.expirationdate", type = ApiParamType.LONG)
    private Long expirationDate;
    @EntityField(name = "nfd.licensevo.entityfield.name.enddate", type = ApiParamType.LONG)
    private Long endDate;
    @EntityField(name = "nfd.licensevo.entityfield.name.graceperiod", type = ApiParamType.INTEGER)
    private int gracePeriod;
    @JSONField(serialize = false)
    private List<String> modules;
    @EntityField(name = "nfd.licensevo.entityfield.name.modules", type = ApiParamType.JSONARRAY)
    private List<ModuleVo> moduleList;
    @EntityField(name = "common.isexpired", type = ApiParamType.BOOLEAN)
    private Boolean isExpired;
    @EntityField(name = "nfd.licensevo.entityfield.name.isend", type = ApiParamType.BOOLEAN)
    private Boolean isEnd;
    @EntityField(name = "nfd.licensevo.entityfield.name.isvalid", type = ApiParamType.BOOLEAN)
    private Boolean isValid;
    @EntityField(name = "模块规则", type = ApiParamType.JSONOBJECT)
    private List<LicenseModuleVo> modulesPolicy;

    public Boolean getIsValid() {
        return StringUtils.isNotBlank(dbUrl) && Config.DB_URL().equalsIgnoreCase(dbUrl);
    }

    public Long getEndDate() {
        if(expirationDate != null) {
            return expirationDate + (long) gracePeriod * 24 * 60 * 60 * 1000;
        }
        return null;
    }


    public Boolean getIsExpired() {
        return expirationDate != null && expirationDate < System.currentTimeMillis();
    }

    public Boolean getIsEnd() {
        return expirationDate != null && expirationDate + (long) gracePeriod * 24 * 60 * 60 * 1000 < System.currentTimeMillis();
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String purchaser) {
        this.purchaser = purchaser;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public List<String> getModules() {
        return modules;
    }

    public List<ModuleVo> getModuleList() {
        List<ModuleVo> moduleList = new ArrayList<>();
        for (String module : modules) {
            moduleList.add(ModuleUtil.getModuleById(module));
        }
        return moduleList;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public List<LicenseModuleVo> getModulesPolicy() {
        return modulesPolicy;
    }

    public void setModulesPolicy(List<LicenseModuleVo> modulesPolicy) {
        this.modulesPolicy = modulesPolicy;
    }
}
