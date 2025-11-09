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

package neatlogic.module.framework.mq.heartbreak.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.heartbeat.core.IHeartbreakHandler;
import neatlogic.framework.mq.core.SubscribeManager;
import neatlogic.framework.mq.dao.mapper.MqSubscribeMapper;
import neatlogic.framework.mq.dto.SubscribeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SubscribeHeartbreakHandler implements IHeartbreakHandler {
    Logger logger = LoggerFactory.getLogger(SubscribeHeartbreakHandler.class);
    @Resource
    private MqSubscribeMapper mqSubscribeMapper;

    @Resource
    private TenantMapper tenantMapper;

    @Override
    public void whenServerInactivated(Integer serverId) {
        //切换到核心库
        List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();

        for (TenantVo tenantVo : tenantList) {
            // 切换到租户库
            TenantContext.get().switchTenant(tenantVo.getUuid());

            SubscribeVo subscribeVo = new SubscribeVo();
            subscribeVo.setIsActive(1);
            subscribeVo.setPageSize(100);
            subscribeVo.setCurrentPage(1);
            //subscribeVo.setServerId(serverId);
            List<SubscribeVo> subList = mqSubscribeMapper.searchSubscribe(subscribeVo);
            while (CollectionUtils.isNotEmpty(subList)) {
                for (SubscribeVo subVo : subList) {
                    //if (subVo.getServerId().equals(serverId)) {
                    //subVo.setServerId(Config.SCHEDULE_SERVER_ID);
                    //mqSubscribeMapper.updateSubscribeServerId(subVo);
                    try {
                        SubscribeManager.create(subVo);
                    } catch (InterruptedException ex) {
                        subVo.setError(StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : ExceptionUtils.getStackTrace(ex));
                        mqSubscribeMapper.updateSubscribeError(subVo);
                        Thread.currentThread().interrupt();
                        logger.error(ex.getMessage(), ex);
                    } catch (Exception ex) {
                        subVo.setError(StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : ExceptionUtils.getStackTrace(ex));
                        mqSubscribeMapper.updateSubscribeError(subVo);
                    }
                    //}
                }
                subscribeVo.setCurrentPage(subscribeVo.getCurrentPage() + 1);
                subList = mqSubscribeMapper.searchSubscribe(subscribeVo);
            }
        }
    }

}
