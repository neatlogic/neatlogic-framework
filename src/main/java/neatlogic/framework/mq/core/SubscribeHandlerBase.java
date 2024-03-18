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
