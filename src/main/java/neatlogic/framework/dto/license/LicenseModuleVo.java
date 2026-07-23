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
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.LicenseUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class LicenseModuleVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1028907252219734230L;
    @EntityField(name = "common.modulegroup", type = ApiParamType.STRING)
    String module;

    @EntityField(name = "nfd.licensevo.entityfield.name.expirationdate", type = ApiParamType.LONG)
    // 兼容历史许可证使用 yyyy-MM-dd 字符串，而字段以毫秒 Long 保存。
    @JSONField(deserializeUsing = LicenseDateDeserializer.class)
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
        if (CollectionUtils.isNotEmpty(policy) && TenantContext.get() != null && StringUtils.isNotBlank(TenantContext.get().getTenantUuid()) &&  LicenseUtil.tenantLicenseInvalidTipsMap.containsKey(TenantContext.get().getTenantUuid())) {
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
