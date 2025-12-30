/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.module.framework.datawarehouse.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.datawarehouse.core.DataSourceServiceHandlerBase;
import neatlogic.framework.datawarehouse.dto.*;
import neatlogic.framework.datawarehouse.exceptions.ReportDataSourceSyncException;
import neatlogic.framework.exception.integration.IntegrationHandlerNotFoundException;
import neatlogic.framework.exception.integration.IntegrationNotFoundException;
import neatlogic.framework.integration.core.IIntegrationHandler;
import neatlogic.framework.integration.core.IntegrationHandlerFactory;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.matrix.exception.MatrixExternalAccessException;
import neatlogic.module.framework.integration.handler.FrameworkRequestFrom;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class IntegrationDataSourceHandler extends DataSourceServiceHandlerBase {

    static Logger logger = LoggerFactory.getLogger(JDBCDataSourceHandler.class);
    int PAGE_SIZE = 10;
    @Resource
    private IntegrationMapper integrationMapper;

    @Override
    public String getHandler() {
        return "integration";
    }

    @Override
    public void mySyncData(DataSourceVo dataSourceVo, DataSourceAuditVo reportDataSourceAuditVo) {
        try {
            JSONObject inputParamObj = new JSONObject();
            List<SelectVo> selectList = getSqlFromDataSource(dataSourceVo);
            if (CollectionUtils.isNotEmpty(selectList)) {
                String inputParamStr = selectList.get(0).getSql();
                if (StringUtils.isNotBlank(inputParamStr)) {
                    try {
                        inputParamObj = JSON.parseObject(inputParamStr);
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
            String integrationUuid = dataSourceVo.getIntegrationUuid();
            IntegrationVo integrationVo = integrationMapper.getIntegrationByUuid(integrationUuid);
            if (integrationVo == null) {
                throw new IntegrationNotFoundException(integrationUuid);
            }
            IIntegrationHandler handler = IntegrationHandlerFactory.getHandler(integrationVo.getHandler());
            if (handler == null) {
                throw new IntegrationHandlerNotFoundException(integrationVo.getHandler());
            }
            JSONObject paramObj = integrationVo.getParamObj();
            paramObj.putAll(inputParamObj);
            paramObj.put("currentPage", 1);
            paramObj.put("pageSize", PAGE_SIZE);
            IntegrationResultVo resultVo = handler.sendRequest(integrationVo, FrameworkRequestFrom.DATAWAREHOUSE);
            if (StringUtils.isNotBlank(resultVo.getError())) {
                logger.error(resultVo.getError());
                throw new MatrixExternalAccessException(integrationVo.getName());
            }
            handler.validate(resultVo);
            JSONObject transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
            Integer rowNum = transformedResult.getInteger("rowNum");
            JSONArray tbodyList = transformedResult.getJSONArray("tbodyList");
            if (CollectionUtils.isNotEmpty(tbodyList)) {
                saveTbodyList(tbodyList, dataSourceVo, reportDataSourceAuditVo);
            }
            if (rowNum != null && rowNum > 0) {
                BasePageVo basePageVo = new BasePageVo();
                basePageVo.setPageSize(PAGE_SIZE);
                basePageVo.setRowNum(rowNum);
                Integer pageCount = basePageVo.getPageCount();
                for (int currentPage = 2; currentPage <= pageCount; currentPage++) {
                    paramObj.put("currentPage", currentPage);
                    resultVo = handler.sendRequest(integrationVo, FrameworkRequestFrom.DATAWAREHOUSE);
                    if (StringUtils.isNotBlank(resultVo.getError())) {
                        logger.error(resultVo.getError());
                        throw new MatrixExternalAccessException(integrationVo.getName());
                    }
                    handler.validate(resultVo);
                    transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
                    tbodyList = transformedResult.getJSONArray("tbodyList");
                    if (CollectionUtils.isNotEmpty(tbodyList)) {
                        saveTbodyList(tbodyList, dataSourceVo, reportDataSourceAuditVo);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            reportDataSourceAuditVo.setError(e.getMessage());
            throw new ReportDataSourceSyncException(dataSourceVo, e);
        }
    }

    private void saveTbodyList(JSONArray tbodyList, DataSourceVo dataSourceVo, DataSourceAuditVo reportDataSourceAuditVo) {
        for (int i = 0; i < tbodyList.size(); i++) {
            JSONObject tbodyObj = tbodyList.getJSONObject(i);
            DataSourceDataVo reportDataSourceDataVo = new DataSourceDataVo(dataSourceVo.getId());
            reportDataSourceDataVo.setExpireMinute(dataSourceVo.getExpireMinute());
            List<DataSourceFieldVo> aggregateFieldList = new ArrayList<>();
            List<DataSourceFieldVo> keyFieldList = new ArrayList<>();
            for (DataSourceFieldVo fieldVo : dataSourceVo.getFieldList()) {
                Object v = tbodyObj.get(fieldVo.getName());
                fieldVo.setValue(v != null ? v : "");//把所有的null值都转成空字符串
                reportDataSourceDataVo.addField(fieldVo);
                if (StringUtils.isNotBlank(fieldVo.getAggregate())) {
                    aggregateFieldList.add(fieldVo);
                }
                if (fieldVo.getIsKey().equals(1)) {
                    keyFieldList.add(fieldVo);
                }
            }
            aggregateAndInsertData(aggregateFieldList, keyFieldList, reportDataSourceDataVo, reportDataSourceAuditVo);
        }
    }
}
