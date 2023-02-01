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

package neatlogic.framework.mq.core;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.mq.dao.mapper.MqSubscribeMapper;
import neatlogic.framework.mq.dto.SubscribeVo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Session;
import javax.jms.TextMessage;

public abstract class SubscribeHandlerBase implements ISubscribeHandler {
    protected static MqSubscribeMapper mqSubscribeMapper;

    @Autowired
    public void setMqSubscribeMapper(MqSubscribeMapper _mqSubscribeMapper) {
        mqSubscribeMapper = _mqSubscribeMapper;
    }


    @Override
    public final void onMessage(TextMessage m, Session session, String topicName, String clientName, String tenantUuid) {
        //由于这是通过MQ监听触发的线程，因此需要自己初始化TenantContext
        TenantContext.init();
        TenantContext.get().switchTenant(tenantUuid).setUseDefaultDatasource(false);
        SubscribeVo subscribeVo = mqSubscribeMapper.getSubscribeByName(clientName);
        //如果订阅已经被删除或被禁用，则直接从删除订阅
        if (subscribeVo != null && subscribeVo.getIsActive().equals(1) && subscribeVo.getServerId().equals(Config.SCHEDULE_SERVER_ID)) {
            //System.out.println(clientName);
            myOnMessage(m);
        } else if (subscribeVo == null || subscribeVo.getIsActive().equals(0)) {
            SubscribeManager.destroy(topicName, clientName);
        }
    }

    protected abstract void myOnMessage(TextMessage m);
}
