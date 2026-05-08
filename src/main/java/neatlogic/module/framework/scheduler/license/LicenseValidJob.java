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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 校验license规则作业
 */
@Component
@DisallowConcurrentExecution
public class LicenseValidJob extends JobBase {
    @Override
    public String getName() {
        return "许可证规则定时校验";
    }

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
                //.withIntervalInSeconds(10)
                .build();
        this.reloadJob(jobObject);
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
        //校验license是否即将过期或已过期
        if (StringUtils.isNotBlank(Config.LICENSE())) {
            Map<String, LicenseInvalidVo> licenseInvalidVoMap = LicenseUtil.tenantLicenseInvalidTipsMap.computeIfAbsent(jobObject.getTenantUuid(), k -> new HashMap<>());
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
                                licenseInvalidVoMap.put(orinModuleGroup, new LicenseInvalidVo(Collections.singletonList(ModuleUtil.getModuleGroup(orinModuleGroup)), LicenseInvalidTypeEnum.ERROR.getValue(), tip));
                            } else if (Boolean.TRUE.equals(licenseModuleVo.getIsExpired())) {
                                String gracePeriod = StringUtils.EMPTY;
                                if (licenseModuleVo.getGracePeriod() > 0) {
                                    gracePeriod = String.format($.t("nmfsl.licensepolicyvalidjob.isexpiredgraceperiod"), licenseModuleVo.getGracePeriod());
                                }
                                String tip = String.format($.t("nmfsl.licensepolicyvalidjob.isexpired"), orinModuleGroupName, orinModuleGroup, TimeUtil.convertDateToString(new Date(licenseModuleVo.getExpirationDate()), TimeUtil.YYYY_MM_DD), gracePeriod);
                                licenseInvalidVoMap.put(orinModuleGroup, new LicenseInvalidVo(Collections.singletonList(ModuleUtil.getModuleGroup(orinModuleGroup)), LicenseInvalidTypeEnum.WARN.getValue(), tip));
                            } else if (Boolean.TRUE.equals(licenseModuleVo.getIsWillExpired())) {
                                licenseInvalidVoMap.put(orinModuleGroup, new LicenseInvalidVo(Collections.singletonList(ModuleUtil.getModuleGroup(orinModuleGroup)), LicenseInvalidTypeEnum.INFO.getValue(), String.format($.t("nmfsl.licensepolicyvalidjob.iswillexpired"), orinModuleGroupName, orinModuleGroup, TimeUtil.convertDateToString(new Date(licenseModuleVo.getExpirationDate()), TimeUtil.YYYY_MM_DD))));
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
}
