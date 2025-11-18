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

package neatlogic.module.framework.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.exception.integration.IntegrationHandlerNotFoundException;
import neatlogic.framework.integration.core.IIntegrationHandler;
import neatlogic.framework.integration.core.IntegrationHandlerFactory;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.matrix.exception.MatrixExternalAccessException;
import neatlogic.framework.scheduler.annotation.Param;
import neatlogic.framework.scheduler.annotation.Prop;
import neatlogic.framework.scheduler.core.PublicJobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.module.framework.integration.handler.FrameworkRequestFrom;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@DisallowConcurrentExecution
public class IntegrationJob extends PublicJobBase {

    static Logger logger = LoggerFactory.getLogger(IntegrationJob.class);

    @Resource
    private IntegrationMapper integrationMapper;

    @Override
    public String getName() {
        return "定时调用集成作业";
    }

    @Prop({
            @Param(name = "supplierIntegrationName", controlType = "text", dataType = "string", required = false, description = "获取数据集成名称", help = "每次执行作业时调用该集成", sort = 0),
            @Param(name = "supplierIntegrationParam", controlType = "json", dataType = "json", required = false, description = "获取数据集成参数", help = "只支持json格式的参数，调用集成时传入该参数", sort = 1),
            @Param(name = "consumerIntegrationName", controlType = "text", dataType = "string", required = false, description = "消费数据集成名称", help = "每次执行作业时调用该集成", sort = 2),
            @Param(name = "consumerIntegrationParam", controlType = "json", dataType = "json", required = false, description = "消费数据集成参数", help = "只支持json格式的参数，调用集成时传入该参数，如果配置了获取数据集成，会把获取数据集成获取到的数据通过supplierData参数传递给消费数据集成", sort = 3)
    })
    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
        JSONObject supplierData = null;
        Object supplierIntegrationName = jobObject.getProp("supplierIntegrationName");
        if (supplierIntegrationName != null) {
            IntegrationVo supplierIntegrationVo = integrationMapper.getIntegrationByName(supplierIntegrationName.toString());
            if (supplierIntegrationVo != null) {
                IIntegrationHandler handler = IntegrationHandlerFactory.getHandler(supplierIntegrationVo.getHandler());
                if (handler == null) {
                    throw new IntegrationHandlerNotFoundException(supplierIntegrationVo.getHandler());
                }
                Object supplierIntegrationParam = jobObject.getProp("supplierIntegrationParam");
                if (supplierIntegrationParam != null) {
                    supplierIntegrationVo.getParamObj().putAll(JSON.parseObject(supplierIntegrationParam.toString()));
                }
                IntegrationResultVo resultVo = handler.sendRequest(supplierIntegrationVo, FrameworkRequestFrom.SCHEDULE);
                if (StringUtils.isNotBlank(resultVo.getError())) {
                    logger.error(resultVo.getError());
                    throw new MatrixExternalAccessException(supplierIntegrationVo.getName());
                }
                handler.validate(resultVo);
                supplierData = JSONObject.parseObject(resultVo.getTransformedResult());
            }
        }

        Object consumerIntegrationName = jobObject.getProp("consumerIntegrationName");
        if (consumerIntegrationName != null) {
            IntegrationVo consumerIntegrationVo = integrationMapper.getIntegrationByName(consumerIntegrationName.toString());
            if (consumerIntegrationVo != null) {
                IIntegrationHandler handler = IntegrationHandlerFactory.getHandler(consumerIntegrationVo.getHandler());
                if (handler == null) {
                    throw new IntegrationHandlerNotFoundException(consumerIntegrationVo.getHandler());
                }
                Object consumerIntegrationParam = jobObject.getProp("consumerIntegrationParam");
                if (consumerIntegrationParam != null) {
                    consumerIntegrationVo.getParamObj().putAll(JSONObject.parseObject(consumerIntegrationParam.toString()));
                }
                if (MapUtils.isNotEmpty(supplierData)) {
                    consumerIntegrationVo.getParamObj().put("supplierData", supplierData);
                }
                IntegrationResultVo resultVo = handler.sendRequest(consumerIntegrationVo, FrameworkRequestFrom.SCHEDULE);
                if (StringUtils.isNotBlank(resultVo.getError())) {
                    logger.error(resultVo.getError());
                    throw new MatrixExternalAccessException(consumerIntegrationVo.getName());
                }
            }
        }
    }
}
