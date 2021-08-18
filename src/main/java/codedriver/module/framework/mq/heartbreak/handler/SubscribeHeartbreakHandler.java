/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.mq.heartbreak.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.TenantMapper;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.exception.mq.SubscribeHandlerNotFoundException;
import codedriver.framework.heartbeat.core.IHeartbreakHandler;
import codedriver.framework.mq.core.ISubscribeHandler;
import codedriver.framework.mq.core.SubscribeHandlerFactory;
import codedriver.framework.mq.core.SubscribeManager;
import codedriver.framework.mq.dao.mapper.MqSubscribeMapper;
import codedriver.framework.mq.dto.SubscribeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SubscribeHeartbreakHandler implements IHeartbreakHandler {

    @Resource
    private MqSubscribeMapper mqSubscribeMapper;

    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public void whenServerInactivated(Integer serverId) {
        //切换到核心库
        TenantContext.get().setUseDefaultDatasource(true);
        List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();

        for (TenantVo tenantVo : tenantList) {
            // 切换到租户库
            TenantContext.get().switchTenant(tenantVo.getUuid()).setUseDefaultDatasource(false);

            SubscribeVo subscribeVo = new SubscribeVo();
            subscribeVo.setIsActive(1);
            List<SubscribeVo> subList = mqSubscribeMapper.searchSubscribe(subscribeVo);
            if (CollectionUtils.isNotEmpty(subList)) {
                for (SubscribeVo subVo : subList) {
                    if (subVo.getServerId().equals(serverId)) {
                        subVo.setServerId(Config.SCHEDULE_SERVER_ID);
                        mqSubscribeMapper.updateSubscribeServerId(subVo);
                        try {
                            ISubscribeHandler subscribeHandler = SubscribeHandlerFactory.getHandler(subVo.getClassName());
                            if (subscribeHandler == null) {
                                throw new SubscribeHandlerNotFoundException(subVo.getClassName());
                            }
                            SubscribeManager.create(subVo.getTopicName(), subVo.getName(), subVo.getIsDurable().equals(1), subscribeHandler);
                        } catch (Exception ex) {
                            subVo.setError(ex.getMessage());
                            mqSubscribeMapper.updateSubscribeError(subVo);
                        }
                    }
                }
            }
        }
    }

}
