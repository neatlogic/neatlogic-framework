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

package neatlogic.module.framework.mq.handler;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.mq.SubscribeHandlerNotFoundException;
import neatlogic.framework.mq.core.ISubscribeHandler;
import neatlogic.framework.mq.core.SubscribeHandlerFactory;
import neatlogic.framework.mq.core.SubscribeManager;
import neatlogic.framework.mq.dao.mapper.MqSubscribeMapper;
import neatlogic.framework.mq.dto.SubscribeVo;
import neatlogic.framework.startup.StartupBase;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SubscribeStartupComponent extends StartupBase {
    private final static Logger logger = LoggerFactory.getLogger(SubscribeStartupComponent.class);
    @Resource
    private MqSubscribeMapper mqSubscribeMapper;

    @Override
    public String getName() {
        return "订阅消息队列主题";
    }

    @Override
    public int executeForCurrentTenant() {
        SubscribeVo subscribeVo = new SubscribeVo();
        subscribeVo.setIsActive(1);
        List<SubscribeVo> subList = mqSubscribeMapper.searchSubscribe(subscribeVo);
        if (CollectionUtils.isNotEmpty(subList)) {
            for (SubscribeVo subVo : subList) {
                if (subVo.getIsActive().equals(1)) {
                    try {
                        ISubscribeHandler subscribeHandler = SubscribeHandlerFactory.getHandler(subVo.getClassName());
                        if (subscribeHandler == null) {
                            throw new SubscribeHandlerNotFoundException(subVo.getClassName());
                        }
                        SubscribeManager.create(subVo.getTopicName(), subVo.getName(), subVo.getIsDurable().equals(1), subscribeHandler);
                        subVo.setError("");
                    } catch (Exception ex) {
                        subVo.setError(ex.getMessage());
                    } finally {
                        mqSubscribeMapper.updateSubscribeError(subVo);
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public int executeForAllTenant() {
        ScheduledExecutorService mqRestartService = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        Runnable runnable = new NeatLogicThread("MQ-SUBSCRIBE-RECONNECT") {
            @Override
            protected void execute() {
                try {
                    Map<String, SimpleMessageListenerContainer> containerMap = SubscribeManager.getListenerMap();
                    if (MapUtils.isNotEmpty(containerMap)) {
                        for (String key : containerMap.keySet()) {
                            SimpleMessageListenerContainer container = containerMap.get(key);
                            String[] keys = key.split(SubscribeManager.SEPARATOR);
                            if (!container.isRunning()) {
                                container.start();
                                SubscribeVo subVo = new SubscribeVo();
                                subVo.setName(keys[2]);
                                subVo.setError("");
                                TenantContext.get().switchTenant(keys[0]).setUseDefaultDatasource(false);
                                mqSubscribeMapper.updateSubscribeError(subVo);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("连接消息队列失败，异常：" + e.getMessage());
                }
            }
        };
        mqRestartService.scheduleAtFixedRate(runnable, Config.MQ_SUBSCRIBE_RECONNECT_PERIOD(), Config.MQ_SUBSCRIBE_RECONNECT_PERIOD(), TimeUnit.MINUTES);
        return 0;
    }

    @Override
    public int sort() {
        return 0;
    }
}
