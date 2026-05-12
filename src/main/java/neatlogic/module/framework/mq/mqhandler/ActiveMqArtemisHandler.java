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

package neatlogic.module.framework.mq.mqhandler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.mq.SubscribeHandlerNotFoundException;
import neatlogic.framework.exception.mq.SubscribeTopicException;
import neatlogic.framework.mq.core.IMqHandler;
import neatlogic.framework.mq.core.ISubscribeHandler;
import neatlogic.framework.mq.core.SubscribeHandlerFactory;
import neatlogic.framework.mq.dto.HealthcheckResultVo;
import neatlogic.framework.mq.dto.SubscribeVo;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveMqArtemisHandler implements IMqHandler {
    private static final Logger logger = LoggerFactory.getLogger(ActiveMqArtemisHandler.class);
    private static final Map<Long, ArtemisConsumerHolder> consumerMap = new ConcurrentHashMap<>();

    private final String brokerUrl = Config.JMS_URL();
    private final String user = Config.JMS_USER();
    private final String password = Config.JMS_PASSWORD();

    @Override
    public String getName() {
        return "artemis";
    }

    @Override
    public String getLabel() {
        return "ActiveMQ Artemis";
    }

    @Override
    public boolean create(SubscribeVo subVo) throws SubscribeTopicException {
        if (!consumerMap.containsKey(subVo.getId())) {
            ISubscribeHandler subscribeHandler = SubscribeHandlerFactory.getHandler(subVo.getClassName());
            if (subscribeHandler == null) {
                throw new SubscribeHandlerNotFoundException(subVo.getClassName());
            }
            String topicName = subVo.getTopicName().toLowerCase();
            String tenantUuid = TenantContext.get().getTenantUuid();
            String queueName = buildQueueName(tenantUuid, topicName);
            subVo.setTenantUuid(TenantContext.get().getTenantUuid());

            ActiveMQConnectionFactory connectionFactory = null;
            Connection connection = null;
            Session session = null;
            MessageConsumer consumer = null;
            try {
                connectionFactory = new ActiveMQConnectionFactory(brokerUrl, user, password);
                connection = connectionFactory.createConnection();
                connection.start();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = new ActiveMQQueue(queueName);
                consumer = session.createConsumer(destination);
                consumer.setMessageListener(message -> {
                    try {
                        subscribeHandler.onMessage(subVo, message);
                    } catch (Exception ex) {
                        logger.error("消费消息失败: {}", ex.getMessage(), ex);
                    }
                });
                consumerMap.put(subVo.getId(), new ArtemisConsumerHolder(connectionFactory, connection, session, consumer));
            } catch (Exception ex) {
                closeQuietly(consumer, "关闭消费者失败");
                closeQuietly(session, "关闭会话失败");
                closeQuietly(connection, "关闭连接失败");
                closeQuietly(connectionFactory, "关闭连接工厂失败");
                throw new SubscribeTopicException(topicName, subVo.getName(), ex.getMessage());
            }
        }
        return true;
    }

    @Override
    public void reconnect(SubscribeVo subscribeVo) throws SubscribeTopicException {
        destroy(subscribeVo);
        create(subscribeVo);
    }

    @Override
    public boolean isRunning(SubscribeVo subscribeVo) {
        return consumerMap.containsKey(subscribeVo.getId());
    }

    @Override
    public void destroy(SubscribeVo subscribeVo) {
        ArtemisConsumerHolder holder = consumerMap.remove(subscribeVo.getId());
        if (holder != null) {
            holder.close();
        }
    }

    @Override
    public void send(String topicName, String content) {
        String queueName = buildQueueName(TenantContext.get().getTenantUuid(), topicName);
        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl, user, password);
             Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            Destination destination = new ActiveMQQueue(queueName);
            MessageProducer producer = session.createProducer(destination);
            TextMessage message = session.createTextMessage(content);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(message);
        } catch (Exception ex) {
            logger.error("发送消息到 Artemis 失败，异常：{}", ex.getMessage());
        }
    }

    @Override
    public boolean isEnable() {
        return brokerUrl != null && !brokerUrl.isEmpty();
    }

    @Override
    public List<HealthcheckResultVo> healthCheck(SubscribeVo subVo) {
        List<HealthcheckResultVo> errorList = new ArrayList<>();
        String tenantUuid = StringUtils.defaultIfBlank(TenantContext.get().getTenantUuid(), subVo.getTenantUuid());
        String queueName = buildQueueName(tenantUuid, subVo.getTopicName());

        try (ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl)) {
            if (StringUtils.isNotBlank(user)) {
                factory.setUser(user);
                factory.setPassword(password);
            }

            try (Connection connection = factory.createConnection()) {
                connection.start();
                try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                    Queue queue = session.createQueue(queueName);
                    try (QueueBrowser browser = session.createBrowser(queue)) {
                        Enumeration<?> messages = browser.getEnumeration();
                        int depth = 0;
                        while (messages.hasMoreElements()) {
                            messages.nextElement();
                            depth++;
                        }
                        if (depth > 1000) {
                            errorList.add(new HealthcheckResultVo("消息消费严重滞后，滞后消息 " + depth + " 条", "error"));
                        } else if (depth > 0) {
                            errorList.add(new HealthcheckResultVo("消息消费存在滞后，滞后消息 " + depth + " 条", "warning"));
                        } else {
                            errorList.add(new HealthcheckResultVo("无消费滞后消息", "normal"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            errorList.add(new HealthcheckResultVo("健康检查失败，异常：" + e.getMessage(), "error"));
        }
        return errorList;
    }

    private static String buildQueueName(String tenantUuid, String topicName) {
        String normalizedTopicName = StringUtils.lowerCase(topicName);
        if (StringUtils.isBlank(tenantUuid)) {
            return normalizedTopicName;
        }
        return tenantUuid + "/" + normalizedTopicName;
    }

    private static void closeQuietly(AutoCloseable closeable, String errorMessage) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                logger.error("{}: {}", errorMessage, e.getMessage(), e);
            }
        }
    }

    private static final class ArtemisConsumerHolder implements AutoCloseable {
        private final Connection connection;
        private final Session session;
        private final MessageConsumer consumer;
        private final ActiveMQConnectionFactory connectionFactory;

        private ArtemisConsumerHolder(ActiveMQConnectionFactory connectionFactory, Connection connection, Session session, MessageConsumer consumer) {
            this.connectionFactory = connectionFactory;
            this.connection = connection;
            this.session = session;
            this.consumer = consumer;
        }

        @Override
        public void close() {
            closeQuietly(consumer, "关闭消费者失败");
            closeQuietly(session, "关闭会话失败");
            closeQuietly(connection, "关闭连接失败");
            closeQuietly(connectionFactory, "关闭连接工厂失败");
        }
    }
}
