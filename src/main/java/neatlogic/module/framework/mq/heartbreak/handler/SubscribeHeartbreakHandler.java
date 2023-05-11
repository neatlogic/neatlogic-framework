/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.mq.heartbreak.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.exception.mq.SubscribeHandlerNotFoundException;
import neatlogic.framework.heartbeat.core.IHeartbreakHandler;
import neatlogic.framework.mq.core.ISubscribeHandler;
import neatlogic.framework.mq.core.SubscribeHandlerFactory;
import neatlogic.framework.mq.core.SubscribeManager;
import neatlogic.framework.mq.dao.mapper.MqSubscribeMapper;
import neatlogic.framework.mq.dto.SubscribeVo;
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
