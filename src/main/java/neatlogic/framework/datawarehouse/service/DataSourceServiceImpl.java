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

package neatlogic.framework.datawarehouse.service;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.datawarehouse.core.DataSourceServiceHandlerFactory;
import neatlogic.framework.datawarehouse.core.IDataSourceServiceHandler;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceAuditMapper;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceDataMapper;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceSchemaMapper;
import neatlogic.framework.datawarehouse.dto.*;
import neatlogic.framework.datawarehouse.enums.Status;
import neatlogic.framework.datawarehouse.exceptions.CreateDataSourceSchemaException;
import neatlogic.framework.datawarehouse.exceptions.DeleteDataSourceSchemaException;
import neatlogic.framework.datawarehouse.exceptions.ReportDataSourceIsSyncingException;
import neatlogic.framework.scheduler.core.IJob;
import neatlogic.framework.scheduler.core.SchedulerManager;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.transaction.core.AfterTransactionJob;
import neatlogic.framework.transaction.core.EscapeTransactionJob;
import neatlogic.module.framework.scheduler.datawarehouse.ReportDataSourceJob;
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

    @Override
    public List<DataSourceFieldVo> revertFieldCondition(List<DataSourceFieldVo> newFieldList, List<DataSourceFieldVo> oldFieldList) {
        if (CollectionUtils.isNotEmpty(newFieldList) && CollectionUtils.isNotEmpty(oldFieldList)) {
            for (DataSourceFieldVo fieldVo : newFieldList) {
                Optional<DataSourceFieldVo> opt = oldFieldList.stream()
                        .filter(o -> Objects.equals(fieldVo.getName(), o.getName()) && Objects.equals(o.getIsCondition(), 1)).findFirst();
                if (opt.isPresent()) {
                    DataSourceFieldVo oldField = opt.get();
                    fieldVo.setIsCondition(1);
                    fieldVo.setInputType(oldField.getInputType());
                    fieldVo.setConfig(oldField.getConfig());
                }
            }
        }
        return newFieldList;
    }
}
