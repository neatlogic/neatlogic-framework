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
