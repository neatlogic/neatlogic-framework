/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.module.framework.scheduler.datawarehouse;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.datawarehouse.dto.DataSourceDataVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import neatlogic.framework.scheduler.core.JobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceDataMapper;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
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
