/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.module.framework.startup;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.mq.core.SubscribeManager;
import neatlogic.framework.mq.dao.mapper.MqSubscribeMapper;
import neatlogic.framework.mq.dto.SubscribeVo;
import neatlogic.framework.startup.StartupBase;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SubscribeStartupComponent extends StartupBase {
    private static final Logger logger = LoggerFactory.getLogger(SubscribeStartupComponent.class);
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
        subscribeVo.setServerId(Config.SCHEDULE_SERVER_ID);
        subscribeVo.setPageSize(100);
        subscribeVo.setCurrentPage(1);
        List<SubscribeVo> subList = mqSubscribeMapper.searchSubscribe(subscribeVo);
        while (CollectionUtils.isNotEmpty(subList)) {
            for (SubscribeVo subVo : subList) {
                if (subVo.getIsActive().equals(1)) {
                    try {
                        SubscribeManager.create(subVo);
                        subVo.setError("");
                    } catch (Exception ex) {
                        subVo.setIsActive(0);
                        subVo.setError(ex.getMessage());
                    } finally {
                        mqSubscribeMapper.updateSubscribeError(subVo);
                    }
                }
            }
            subscribeVo.setCurrentPage(subscribeVo.getCurrentPage() + 1);
            subList = mqSubscribeMapper.searchSubscribe(subscribeVo);
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
                Map<String, List<SubscribeVo>> activeSubscribeMap = SubscribeManager.getActiveSubscribeMap();
                if (MapUtils.isNotEmpty(activeSubscribeMap)) {
                    for (Map.Entry<String, List<SubscribeVo>> entry : activeSubscribeMap.entrySet()) {
                        //切换租户
                        TenantContext.get().switchTenant(entry.getKey());
                        List<SubscribeVo> activeSubscribeList = entry.getValue();
                        for (SubscribeVo subVo : activeSubscribeList) {
                            try {
                                if (!SubscribeManager.needReconnect(subVo)) {
                                    try {
                                        SubscribeManager.reconnect(subVo);
                                        subVo.setError(null);
                                        subVo.setIsActive(1);
                                    } catch (Exception e) {
                                        logger.error(e.getMessage(), e);
                                        subVo.setError(e.getMessage());
                                    } finally {
                                        mqSubscribeMapper.updateSubscribeError(subVo);
                                    }
                                }
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        };
        mqRestartService.scheduleAtFixedRate(runnable, Config.MQ_SUBSCRIBE_RECONNECT_PERIOD(), Config.MQ_SUBSCRIBE_RECONNECT_PERIOD(), TimeUnit.SECONDS);
        return 0;
    }

    @Override
    public int sort() {
        return 0;
    }
}
