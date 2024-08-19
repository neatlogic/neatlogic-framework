/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SubscribeHeartbreakHandler implements IHeartbreakHandler {

    @Resource
    private MqSubscribeMapper mqSubscribeMapper;

    @Resource
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
