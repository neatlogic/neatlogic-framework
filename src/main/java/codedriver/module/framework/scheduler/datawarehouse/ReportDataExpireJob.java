/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.scheduler.datawarehouse;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.datawarehouse.dto.DataSourceDataVo;
import codedriver.framework.datawarehouse.dto.DataSourceVo;
import codedriver.framework.scheduler.core.JobBase;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceDataMapper;
import codedriver.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
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
