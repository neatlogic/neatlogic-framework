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

package neatlogic.module.framework.scheduler.license;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.license.ILicensePolicy;
import neatlogic.framework.common.constvalue.license.LicenseInvalidTypeEnum;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.license.LicenseInvalidVo;
import neatlogic.framework.dto.license.LicenseModuleVo;
import neatlogic.framework.dto.license.LicenseVo;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.scheduler.core.JobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.util.$;
import neatlogic.framework.util.LicenseUtil;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 校验license规则作业
 */
@Component
@DisallowConcurrentExecution
public class LicenseValidJob extends JobBase {
    @Override
    public String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-LICENSE-VALID-GROUP";
    }

    @Override
    public Boolean isMyHealthy(JobObject jobObject) {
        return true;
    }

    @Override
    public void reloadJob(JobObject jobObject) {
        schedulerManager.loadJob(jobObject);
    }

    @Override
    public void initJob(String tenantUuid) {
        //每个小时的50分执行一次
        JobObject jobObject = new JobObject.Builder("LICENSE-VALID-JOB", this.getGroupName(), this.getClassName(), tenantUuid)
                .withCron("0 50 * * * ?")
                //.withIntervalInSeconds(10)//test
                .build();
        this.reloadJob(jobObject);
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
        //校验license是否即将过期或已过期
        LicenseVo licenseVo = LicenseUtil.deLicense(Config.LICENSE(), Config.LICENSE_PK());
        if (licenseVo != null) {
            List<LicenseModuleVo> licenseModuleVos = licenseVo.getModulesPolicy();
            if (CollectionUtils.isNotEmpty(licenseModuleVos)) {
                Map<String, LicenseModuleVo> licenseModuleVoMap = licenseModuleVos.stream().collect(Collectors.toMap(LicenseModuleVo::getModule, e -> e));
                List<ModuleGroupVo> moduleGroupVos = ModuleUtil.getAllModuleGroupList();
                for (ModuleGroupVo moduleGroupVo : moduleGroupVos) {
                    String orinModuleGroup = moduleGroupVo.getGroup();
                    String orinModuleGroupName = moduleGroupVo.getGroupName();
                    LicenseModuleVo licenseModuleVo = licenseModuleVoMap.get(orinModuleGroup);
                    if (Boolean.TRUE.equals(licenseModuleVo != null && licenseModuleVoMap.get(orinModuleGroup).getIsBanModule() == 1)) {
                        if (Boolean.TRUE.equals(licenseModuleVo.getIsEnd())) {
                            String gracePeriod = StringUtils.EMPTY;
                            if (licenseModuleVo.getGracePeriod() > 0) {
                                gracePeriod = String.format($.t("nmfsl.licensepolicyvalidjob.isendgraceperiod"), licenseModuleVo.getGracePeriod());
                            }
                            String tip = String.format($.t("nmfsl.licensepolicyvalidjob.isend"), orinModuleGroupName, orinModuleGroup, TimeUtil.convertDateToString(new Date(licenseModuleVo.getExpirationDate()), TimeUtil.YYYY_MM_DD), gracePeriod);
                            LicenseUtil.licenseInvalidTipsMap.put(orinModuleGroup, new LicenseInvalidVo(Collections.singletonList(ModuleUtil.getModuleGroup(orinModuleGroup)), LicenseInvalidTypeEnum.ERROR.getValue(), tip));
                        } else if (Boolean.TRUE.equals(licenseModuleVo.getIsExpired())) {
                            String gracePeriod = StringUtils.EMPTY;
                            if (licenseModuleVo.getGracePeriod() > 0) {
                                gracePeriod = String.format($.t("nmfsl.licensepolicyvalidjob.isexpiredgraceperiod"), licenseModuleVo.getGracePeriod());
                            }
                            String tip = String.format($.t("nmfsl.licensepolicyvalidjob.isexpired"), orinModuleGroupName, orinModuleGroup, TimeUtil.convertDateToString(new Date(licenseModuleVo.getExpirationDate()), TimeUtil.YYYY_MM_DD), gracePeriod);
                            LicenseUtil.licenseInvalidTipsMap.put(orinModuleGroup, new LicenseInvalidVo(Collections.singletonList(ModuleUtil.getModuleGroup(orinModuleGroup)), LicenseInvalidTypeEnum.WARN.getValue(), tip));
                        } else if (Boolean.TRUE.equals(licenseModuleVo.getIsWillExpired())) {
                            LicenseUtil.licenseInvalidTipsMap.put(orinModuleGroup, new LicenseInvalidVo(Collections.singletonList(ModuleUtil.getModuleGroup(orinModuleGroup)), LicenseInvalidTypeEnum.INFO.getValue(), String.format($.t("nmfsl.licensepolicyvalidjob.iswillexpired"), orinModuleGroupName, orinModuleGroup, TimeUtil.convertDateToString(new Date(licenseModuleVo.getExpirationDate()), TimeUtil.YYYY_MM_DD))));
                        }
                    }
                }
            }
        }
        //校验所有规则
        Map<String, ILicensePolicy> licensePolicyMap = LicenseUtil.licensePolicyMap;
        for (ILicensePolicy licensePolicy : licensePolicyMap.values()) {
            licensePolicy.valid();
        }
    }
}
