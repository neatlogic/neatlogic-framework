/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.dto.license;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.LicenseUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

public class LicenseModuleVo implements Serializable {
    @EntityField(name = "common.modulegroup", type = ApiParamType.STRING)
    String module;

    @EntityField(name = "nfd.licensevo.entityfield.name.expirationdate", type = ApiParamType.LONG)
    private Long expirationDate;

    @EntityField(name = "common.policy", type = ApiParamType.JSONOBJECT)
    List<LicenseModulePolicyVo> policy;

    @EntityField(name = "nfd.licensevo.entityfield.name.graceperiod", type = ApiParamType.INTEGER)
    private int gracePeriod = 0;
    @EntityField(name = "到期禁用模块", type = ApiParamType.INTEGER)
    private int isBanModule;


    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getIsWillExpired() {
        return expirationDate != null && (expirationDate - (long) Config.LICENSE_WILL_EXPIRED_NOTIFY_DAY() * 24 * 60 * 60 * 1000) < System.currentTimeMillis();
    }

    public Boolean getIsExpired() {
        return expirationDate != null && expirationDate < System.currentTimeMillis();
    }

    public Boolean getIsEnd() {
        return expirationDate != null && expirationDate + (long) gracePeriod * 24 * 60 * 60 * 1000 < System.currentTimeMillis();
    }

    public Boolean getIsInvalidPolicy() {
        if (CollectionUtils.isNotEmpty(policy) && TenantContext.get() != null && StringUtils.isNotBlank(TenantContext.get().getTenantUuid())) {
            return policy.stream().anyMatch(p -> LicenseUtil.tenantLicenseInvalidTipsMap.get(TenantContext.get().getTenantUuid()).containsKey(p.getKey()));
        }
        return false;
    }


    public List<LicenseModulePolicyVo> getPolicy() {
        return policy;
    }

    public void setPolicy(List<LicenseModulePolicyVo> policy) {
        this.policy = policy;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public int getIsBanModule() {
        return isBanModule;
    }

    public void setIsBanModule(int isBanModule) {
        this.isBanModule = isBanModule;
    }
}
