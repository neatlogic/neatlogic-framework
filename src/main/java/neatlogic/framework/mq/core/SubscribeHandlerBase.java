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

package neatlogic.framework.mq.core;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.mq.dao.mapper.MqSubscribeMapper;
import neatlogic.framework.mq.dto.SubscribeVo;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SubscribeHandlerBase implements ISubscribeHandler {
    protected static MqSubscribeMapper mqSubscribeMapper;

    @Autowired
    public void setMqSubscribeMapper(MqSubscribeMapper _mqSubscribeMapper) {
        mqSubscribeMapper = _mqSubscribeMapper;
    }

    @Override
    public final void onMessage(SubscribeVo subscribeVo, Object message) {
        //由于这是通过MQ监听触发的线程，因此需要自己初始化TenantContext
        TenantContext.init();
        TenantContext.get().switchTenant(subscribeVo.getTenantUuid());
        //从DB再查一次订阅信息，检查订阅是否仍然有效
        SubscribeVo checkSubscribeVo = mqSubscribeMapper.getSubscribeByName(subscribeVo.getName());
        //如果订阅已经被删除或被禁用，则直接从删除订阅
        if (checkSubscribeVo != null && checkSubscribeVo.getIsActive().equals(1) /*&& checkSubscribeVo.getServerId().equals(Config.SCHEDULE_SERVER_ID)*/) {
            //System.out.println(clientName);
            myOnMessage(checkSubscribeVo, message);
        } else if (checkSubscribeVo != null && checkSubscribeVo.getIsActive().equals(0)) {
            SubscribeManager.destroy(subscribeVo);
        }
    }


   /* @Override
    public final void onMessage(TextMessage m, Session session, String topicName, String clientName, String tenantUuid) {
        //由于这是通过MQ监听触发的线程，因此需要自己初始化TenantContext
        TenantContext.init();
        TenantContext.get().switchTenant(tenantUuid).setUseDefaultDatasource(false);
        SubscribeVo subscribeVo = mqSubscribeMapper.getSubscribeByName(clientName);
        //如果订阅已经被删除或被禁用，则直接从删除订阅
        if (subscribeVo != null && subscribeVo.getIsActive().equals(1) && subscribeVo.getServerId().equals(Config.SCHEDULE_SERVER_ID)) {
            //System.out.println(clientName);
            myOnMessage(m);
        } else if (subscribeVo != null && subscribeVo.getIsActive().equals(0)) {
            SubscribeManager.destroy(subscribeVo);
        }
    }*/

    //protected abstract void myOnMessage(TextMessage m);

    protected abstract void myOnMessage(SubscribeVo subscribeVo, Object message);
}
