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

package neatlogic.framework.dto;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LicenseVo {
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

    public Boolean getIsValid() {
        return expirationDate != null && StringUtils.isNotBlank(dbUrl) && Config.DB_URL().equalsIgnoreCase(dbUrl);
    }

    public Long getEndDate() {
        return expirationDate + (long) gracePeriod * 24 * 60 * 60 * 1000;
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
}
