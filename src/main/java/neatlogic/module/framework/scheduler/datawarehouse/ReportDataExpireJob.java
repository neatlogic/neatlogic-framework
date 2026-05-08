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
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceDataMapper;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import neatlogic.framework.datawarehouse.dto.DataSourceDataVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import neatlogic.framework.scheduler.core.JobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 报表数据源数据清理定时器
 */
@Component
@DisallowConcurrentExecution
public class ReportDataExpireJob extends JobBase {
    @Override
    public String getName() {
        return "数据仓库过期数据清理";
    }

    //static Logger logger = LoggerFactory.getLogger(ReportDataSourceJob.class);

    @Resource
    private DataWarehouseDataSourceMapper reportDataSourceMapper;

    @Resource
    private DataWarehouseDataSourceDataMapper reportDataSourceDataMapper;

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
        //每天凌晨1点运行
        JobObject jobObject = new JobObject.Builder("REPORT-DATA-EXPIRE-JOB", this.getGroupName(), this.getClassName(), tenantUuid)
                .withCron("0 0 1 * * ?")
                //.withCron("0 * * * * ?")//测试用
                .build();
        this.reloadJob(jobObject);
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) {
        DataSourceVo pReportDataSourceVo = new DataSourceVo();
        pReportDataSourceVo.setPageSize(100);
        pReportDataSourceVo.setCurrentPage(1);
        pReportDataSourceVo.setIsActive(1);
        List<DataSourceVo> reportDataSourceList = reportDataSourceMapper.searchDataSource(pReportDataSourceVo);
        while (CollectionUtils.isNotEmpty(reportDataSourceList)) {
            for (DataSourceVo reportDataSourceVo : reportDataSourceList) {
                DataSourceDataVo dataVo = new DataSourceDataVo(reportDataSourceVo.getId());
                if (reportDataSourceDataMapper.clearExpiredData(dataVo) > 0) {
                    reportDataSourceVo.setDataCount(reportDataSourceDataMapper.getDataSourceDataCount(dataVo));
                    reportDataSourceMapper.updateReportDataSourceDataCount(reportDataSourceVo);
                }
            }
            pReportDataSourceVo.setCurrentPage(pReportDataSourceVo.getCurrentPage() + 1);
            reportDataSourceList = reportDataSourceMapper.searchDataSource(pReportDataSourceVo);
        }
    }


    @Override
    public String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-REPORT-DATA-EXPIRE-JOB";
    }

}
