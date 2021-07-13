/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.mq.core;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.mq.dao.mapper.MqSubscribeMapper;
import codedriver.framework.mq.dto.SubscribeVo;
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
        if (subscribeVo.getIsActive().equals(1) && subscribeVo.getServerId().equals(Config.SCHEDULE_SERVER_ID)) {
            myOnMessage(m);
        } else if (subscribeVo.getIsActive().equals(0)) {
            SubscribeManager.destory(topicName, clientName);
        }
    }

    protected abstract void myOnMessage(TextMessage m);
}
