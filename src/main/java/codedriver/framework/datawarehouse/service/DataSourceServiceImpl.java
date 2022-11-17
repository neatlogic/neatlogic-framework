/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.service;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.datawarehouse.core.DataSourceServiceHandlerFactory;
import codedriver.framework.datawarehouse.core.IDataSourceServiceHandler;
import codedriver.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceAuditMapper;
import codedriver.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceDataMapper;
import codedriver.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import codedriver.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceSchemaMapper;
import codedriver.framework.datawarehouse.dto.*;
import codedriver.framework.datawarehouse.enums.Status;
import codedriver.framework.datawarehouse.exceptions.CreateDataSourceSchemaException;
import codedriver.framework.datawarehouse.exceptions.DeleteDataSourceSchemaException;
import codedriver.framework.datawarehouse.exceptions.ReportDataSourceIsSyncingException;
import codedriver.framework.scheduler.core.IJob;
import codedriver.framework.scheduler.core.SchedulerManager;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.transaction.core.AfterTransactionJob;
import codedriver.framework.transaction.core.EscapeTransactionJob;
import codedriver.module.framework.scheduler.datawarehouse.ReportDataSourceJob;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DataSourceServiceImpl implements DataSourceService {
    static Logger logger = LoggerFactory.getLogger(DataSourceServiceImpl.class);

    @Resource
    private SchedulerManager schedulerManager;

    @Resource
    private DataWarehouseDataSourceMapper dataSourceMapper;

    @Resource
    private DataWarehouseDataSourceDataMapper dataSourceDataMapper;

    @Resource
    private DataWarehouseDataSourceSchemaMapper dataSourceSchemaMapper;

    @Resource
    private DataWarehouseDataSourceAuditMapper dataSourceAuditMapper;


    @Override
    public void deleteReportDataSource(DataSourceVo reportDataSourceVo) {
        if (reportDataSourceVo != null) {
            dataSourceMapper.deleteDataSourceFieldByDataSourceId(reportDataSourceVo.getId());
            dataSourceMapper.deleteDataSourceParamByDataSourceId(reportDataSourceVo.getId());
            dataSourceMapper.deleteReportDataSourceById(reportDataSourceVo.getId());
            dataSourceAuditMapper.deleteReportDataSourceAuditByDatasourceId(reportDataSourceVo.getId());
            //由于以下操作是DDL操作，所以需要使用EscapeTransactionJob避开当前事务，否则在进行DDL操作之前事务就会提交，如果DDL出错，则上面的事务就无法回滚了
            EscapeTransactionJob.State s = new EscapeTransactionJob(() -> dataSourceSchemaMapper.deleteDataSourceTable(reportDataSourceVo)).execute();
            if (!s.isSucceed()) {
                throw new DeleteDataSourceSchemaException(reportDataSourceVo);
            }
        }
    }

    @Override
    public void executeReportDataSource(DataSourceVo pDataSourceVo) {
        if (pDataSourceVo != null && CollectionUtils.isNotEmpty(pDataSourceVo.getFieldList())) {
            if (Objects.equals(pDataSourceVo.getStatus(), Status.DOING.getValue())) {
                throw new ReportDataSourceIsSyncingException(pDataSourceVo);
            }
            //更新数据源状态，写入审计信息
            pDataSourceVo.setStatus(Status.DOING.getValue());
            dataSourceMapper.updateReportDataSourceStatus(pDataSourceVo);
            DataSourceAuditVo reportDataSourceAuditVo = new DataSourceAuditVo();
            reportDataSourceAuditVo.setDataSourceId(pDataSourceVo.getId());
            dataSourceAuditMapper.insertReportDataSourceAudit(reportDataSourceAuditVo);
            AfterTransactionJob<DataSourceVo> afterTransactionJob = new AfterTransactionJob<>("REPORT-DATASOURCE-SYNC");
            afterTransactionJob.execute(pDataSourceVo, dataSourceVo -> {
                IDataSourceServiceHandler dataSourceServiceHandler = DataSourceServiceHandlerFactory.getHandler(dataSourceVo.getDbType());
                try {
                    dataSourceServiceHandler.syncData(dataSourceVo, reportDataSourceAuditVo);
                    dataSourceVo.setStatus(Status.DONE.getValue());
                } catch (Exception ex) {
                    dataSourceVo.setStatus(Status.FAILED.getValue());
                    reportDataSourceAuditVo.setError(ex.getMessage());
                } finally {
                    int dataCount = dataSourceDataMapper.getDataSourceDataCount(new DataSourceDataVo(dataSourceVo.getId()));
                    dataSourceVo.setDataCount(dataCount);
                    dataSourceMapper.updateReportDataSourceStatus(dataSourceVo);
                    dataSourceAuditMapper.updateReportDataSourceAudit(reportDataSourceAuditVo);
                }
            });
        }
    }

    @Override
    public void insertDataSource(DataSourceVo vo) {
        dataSourceMapper.insertDataSource(vo);
        if (CollectionUtils.isNotEmpty(vo.getFieldList())) {
            for (DataSourceFieldVo field : vo.getFieldList()) {
                field.setDataSourceId(vo.getId());
                dataSourceMapper.insertDataSourceField(field);
            }
        }
        if (CollectionUtils.isNotEmpty(vo.getParamList())) {
            for (DataSourceParamVo param : vo.getParamList()) {
                param.setDataSourceId(vo.getId());
                dataSourceMapper.insertDataSourceParam(param);
            }
        }
    }

    @Override
    public void updateDataSource(DataSourceVo newVo, DataSourceVo newXmlConfig, DataSourceVo oldVo) {
        List<DataSourceFieldVo> deleteFieldList = oldVo.getFieldList().stream().filter(d -> !newXmlConfig.getFieldList().contains(d)).collect(Collectors.toList());
        List<DataSourceFieldVo> updateFieldList = newXmlConfig.getFieldList().stream().filter(d -> oldVo.getFieldList().contains(d)).collect(Collectors.toList());
        List<DataSourceFieldVo> insertFieldList = newXmlConfig.getFieldList().stream().filter(d -> !oldVo.getFieldList().contains(d)).collect(Collectors.toList());
        //用回旧的fieldId
        if (CollectionUtils.isNotEmpty(updateFieldList)) {
            for (DataSourceFieldVo field : updateFieldList) {
                Optional<DataSourceFieldVo> op = oldVo.getFieldList().stream().filter(d -> d.equals(field)).findFirst();
                op.ifPresent(dataSourceFieldVo -> field.setId(dataSourceFieldVo.getId()));
            }
        }
        newVo.setFieldList(null);//清空旧数据
        newVo.addField(insertFieldList);
        newVo.addField(updateFieldList);

        List<DataSourceParamVo> deleteParamList = oldVo.getParamList().stream().filter(d -> !newXmlConfig.getParamList().contains(d)).collect(Collectors.toList());
        List<DataSourceParamVo> updateParamList = newXmlConfig.getParamList().stream().filter(d -> oldVo.getParamList().contains(d)).collect(Collectors.toList());
        List<DataSourceParamVo> insertParamList = newXmlConfig.getParamList().stream().filter(d -> !oldVo.getParamList().contains(d)).collect(Collectors.toList());
        //用回旧的paramId
        if (CollectionUtils.isNotEmpty(updateParamList)) {
            for (DataSourceParamVo param : updateParamList) {
                Optional<DataSourceParamVo> op = oldVo.getParamList().stream().filter(d -> d.equals(param)).findFirst();
                op.ifPresent(dataSourceParamVo -> param.setId(dataSourceParamVo.getId()));
            }
        }
        newVo.setParamList(null);//清空旧数据
        newVo.addParam(insertParamList);
        newVo.addParam(updateParamList);

        //FIXME 检查数据源是否被使用
        dataSourceMapper.updateDataSource(newVo);

        if (CollectionUtils.isNotEmpty(deleteFieldList)) {
            dataSourceMapper.deleteDataSourceFieldByIdList(deleteFieldList.stream().map(DataSourceFieldVo::getId).collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(updateFieldList)) {
            for (DataSourceFieldVo field : updateFieldList) {
                dataSourceMapper.updateDataSourceField(field);
            }
        }

        if (CollectionUtils.isNotEmpty(insertFieldList)) {
            for (DataSourceFieldVo field : insertFieldList) {
                field.setDataSourceId(newVo.getId());
            }
            dataSourceMapper.batchInsertDataSourceField(insertFieldList);
        }

        if (CollectionUtils.isNotEmpty(deleteParamList)) {
            dataSourceMapper.deleteDataSourceParamByIdList(deleteParamList.stream().map(DataSourceParamVo::getId).collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(updateParamList)) {
            for (DataSourceParamVo param : updateParamList) {
                dataSourceMapper.updateDataSourceParam(param);
            }
        }

        if (CollectionUtils.isNotEmpty(insertParamList)) {
            for (DataSourceParamVo param : insertParamList) {
                param.setDataSourceId(newVo.getId());
            }
            dataSourceMapper.batchInsertDataSourceParam(insertParamList);
        }
    }

    @Override
    public void createDataSourceSchema(DataSourceVo dataSourceVo) {
        //由于以下操作是DDL操作，所以需要使用EscapeTransactionJob避开当前事务，否则在进行DDL操作之前事务就会提交，如果DDL出错，则上面的事务就无法回滚了
        EscapeTransactionJob.State s = new EscapeTransactionJob(() -> {
            dataSourceSchemaMapper.deleteDataSourceTable(dataSourceVo);
            dataSourceSchemaMapper.createDataSourceTable(dataSourceVo);
        }).execute();
        if (!s.isSucceed()) {
            throw new CreateDataSourceSchemaException(dataSourceVo);
        }
    }

    @Override
    public void loadOrUnloadReportDataSourceJob(DataSourceVo dataSourceVo) {
        String tenantUuid = TenantContext.get().getTenantUuid();
        IJob jobHandler = SchedulerManager.getHandler(ReportDataSourceJob.class.getName());
        JobObject jobObject = new JobObject.Builder(dataSourceVo.getId().toString(), jobHandler.getGroupName(), jobHandler.getClassName(), tenantUuid)
                .withCron(dataSourceVo.getCronExpression())
                .addData("datasourceId", dataSourceVo.getId())
                .build();
        if (StringUtils.isNotBlank(dataSourceVo.getCronExpression())) {
            schedulerManager.loadJob(jobObject);
        } else {
            schedulerManager.unloadJob(jobObject);
        }
    }
}
