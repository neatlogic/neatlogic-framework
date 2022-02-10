/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.mq.handler;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.exception.mq.SubscribeHandlerNotFoundException;
import codedriver.framework.mq.core.ISubscribeHandler;
import codedriver.framework.mq.core.SubscribeHandlerFactory;
import codedriver.framework.mq.core.SubscribeManager;
import codedriver.framework.mq.dao.mapper.MqSubscribeMapper;
import codedriver.framework.mq.dto.SubscribeVo;
import codedriver.framework.startup.IStartup;
import codedriver.framework.startup.StartupBase;
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
    public void executeForCurrentTenant() {
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
    }

    @Override
    public void executeForAllTenant() {
        ScheduledExecutorService mqRestartService = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        Runnable runnable = new CodeDriverThread("MQ-SUBSCRIBE-RECONNECT") {
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
    }

    @Override
    public int sort() {
        return 0;
    }
}
