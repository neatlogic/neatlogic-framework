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

package neatlogic.module.framework.scheduler.datawarehouse;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import neatlogic.framework.datawarehouse.service.DataSourceService;
import neatlogic.framework.scheduler.core.JobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 报表数据源数据同步定时器
 */
@Component
@DisallowConcurrentExecution
public class ReportDataSourceJob extends JobBase {
    @Override
    public String getName() {
        return "数据仓库数据源同步";
    }

    //static Logger logger = LoggerFactory.getLogger(ReportDataSourceJob.class);

    @Resource
    private DataWarehouseDataSourceMapper reportDataSourceMapper;

    @Resource
    private DataSourceService reportDataSourceService;


    @Override
    public Boolean isMyHealthy(JobObject jobObject) {
        DataSourceVo dataSourceVo = reportDataSourceMapper.getDataSourceById(Long.valueOf(jobObject.getJobName()));
        if (dataSourceVo != null) {
            return dataSourceVo.getIsActive().equals(1) && dataSourceVo.getCronExpression().equals(jobObject.getCron());
        }
        return false;
    }

    @Override
    public void reloadJob(JobObject jobObject) {
        String tenantUuid = jobObject.getTenantUuid();
        TenantContext.get().switchTenant(tenantUuid);
        DataSourceVo dataSourceVo = reportDataSourceMapper.getDataSourceById(Long.valueOf(jobObject.getJobName()));
        if (dataSourceVo != null && Objects.equals(dataSourceVo.getIsActive(), 1)) {
            JobObject newJobObject = new JobObject.Builder(dataSourceVo.getId().toString(), this.getGroupName(), this.getClassName(), tenantUuid)
                    .withCron(dataSourceVo.getCronExpression())
                    .addData("datasourceId", dataSourceVo.getId())
                    .build();
            schedulerManager.loadJob(newJobObject);
        } else {
            schedulerManager.unloadJob(jobObject);
        }
    }

    @Override
    public void initJob(String tenantUuid) {
        List<DataSourceVo> dataSourceList = reportDataSourceMapper.getAllHasCronReportDataSource();
        if (CollectionUtils.isNotEmpty(dataSourceList)) {
            for (DataSourceVo vo : dataSourceList) {
                JobObject newJobObject = new JobObject.Builder(vo.getId().toString(), this.getGroupName(), this.getClassName(), tenantUuid)
                        .withCron(vo.getCronExpression())
                        .addData("datasourceId", vo.getId())
                        .build();
                schedulerManager.loadJob(newJobObject);
            }
        }
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws SQLException {
        Long datasourceId = (Long) jobObject.getData("datasourceId");
        DataSourceVo reportDataSourceVo = reportDataSourceMapper.getDataSourceById(datasourceId);
        reportDataSourceService.executeReportDataSource(reportDataSourceVo);
        reportDataSourceVo.setLastFireTime(context.getFireTime());
        reportDataSourceVo.setLastFinishTime(new Date());
        reportDataSourceVo.setNextFireTime(context.getNextFireTime());
        reportDataSourceMapper.updateDataSourceJobTimeById(reportDataSourceVo);
    }


    @Override
    public String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-REPORT-DATASOURCE-SYNC";
    }

}
