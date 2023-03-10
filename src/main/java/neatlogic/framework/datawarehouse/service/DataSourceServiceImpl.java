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
            //?????????????????????DDL???????????????????????????EscapeTransactionJob????????????????????????????????????DDL???????????????????????????????????????DDL?????????????????????????????????????????????
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
            //??????????????????????????????????????????
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
        //????????????fieldId
        if (CollectionUtils.isNotEmpty(updateFieldList)) {
            for (DataSourceFieldVo field : updateFieldList) {
                Optional<DataSourceFieldVo> op = oldVo.getFieldList().stream().filter(d -> d.equals(field)).findFirst();
                op.ifPresent(dataSourceFieldVo -> field.setId(dataSourceFieldVo.getId()));
            }
        }
        newVo.setFieldList(null);//???????????????
        newVo.addField(insertFieldList);
        newVo.addField(updateFieldList);

        List<DataSourceParamVo> deleteParamList = oldVo.getParamList().stream().filter(d -> !newXmlConfig.getParamList().contains(d)).collect(Collectors.toList());
        List<DataSourceParamVo> updateParamList = newXmlConfig.getParamList().stream().filter(d -> oldVo.getParamList().contains(d)).collect(Collectors.toList());
        List<DataSourceParamVo> insertParamList = newXmlConfig.getParamList().stream().filter(d -> !oldVo.getParamList().contains(d)).collect(Collectors.toList());
        //????????????paramId
        if (CollectionUtils.isNotEmpty(updateParamList)) {
            for (DataSourceParamVo param : updateParamList) {
                Optional<DataSourceParamVo> op = oldVo.getParamList().stream().filter(d -> d.equals(param)).findFirst();
                op.ifPresent(dataSourceParamVo -> param.setId(dataSourceParamVo.getId()));
            }
        }
        newVo.setParamList(null);//???????????????
        newVo.addParam(insertParamList);
        newVo.addParam(updateParamList);

        //FIXME ??????????????????????????????
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
        //?????????????????????DDL???????????????????????????EscapeTransactionJob????????????????????????????????????DDL???????????????????????????????????????DDL?????????????????????????????????????????????
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
