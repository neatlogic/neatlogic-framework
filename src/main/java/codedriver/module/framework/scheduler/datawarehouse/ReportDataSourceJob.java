/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.scheduler.datawarehouse;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import codedriver.framework.datawarehouse.dto.ReportDataSourceVo;
import codedriver.framework.datawarehouse.service.ReportDataSourceService;
import codedriver.framework.scheduler.core.JobBase;
import codedriver.framework.scheduler.dto.JobObject;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * 报表数据源数据同步定时器
 */
@Component
public class ReportDataSourceJob extends JobBase {
    //static Logger logger = LoggerFactory.getLogger(ReportDataSourceJob.class);

    @Resource
    private DataWarehouseDataSourceMapper reportDataSourceMapper;

    @Resource
    private ReportDataSourceService reportDataSourceService;


    @Override
    public Boolean isHealthy(JobObject jobObject) {
        ReportDataSourceVo dataSourceVo = reportDataSourceMapper.getReportDataSourceById(Long.valueOf(jobObject.getJobName()));
        if (dataSourceVo != null) {
            return dataSourceVo.getIsActive().equals(1) && dataSourceVo.getCronExpression().equals(jobObject.getCron());
        }
        return false;
    }

    @Override
    public void reloadJob(JobObject jobObject) {
        String tenantUuid = jobObject.getTenantUuid();
        TenantContext.get().switchTenant(tenantUuid);
        ReportDataSourceVo dataSourceVo = reportDataSourceMapper.getReportDataSourceById(Long.valueOf(jobObject.getJobName()));
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
        List<ReportDataSourceVo> dataSourceList = reportDataSourceMapper.getAllHasCronReportDataSource();
        if (CollectionUtils.isNotEmpty(dataSourceList)) {
            for (ReportDataSourceVo vo : dataSourceList) {
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
        ReportDataSourceVo reportDataSourceVo = reportDataSourceMapper.getReportDataSourceById(datasourceId);
        reportDataSourceService.executeReportDataSource(reportDataSourceVo);
    }


    @Override
    public String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-REPORT-DATASOURCE-SYNC";
    }

}
