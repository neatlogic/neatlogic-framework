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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.JmsUtils;
import org.springframework.stereotype.Component;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveMqArtemisTopicHandler implements IMqHandler {
    private static final Logger logger = LoggerFactory.getLogger(ActiveMqArtemisTopicHandler.class);
    private static final Map<Long, ArtemisTopicConsumerHolder> consumerMap = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "artemis-topic";
    }

    @Override
    public String getLabel() {
        return "ActiveMQ Artemis Topic";
    }

    @Override
    public boolean create(SubscribeVo subVo) throws SubscribeTopicException {
        if (consumerMap.containsKey(subVo.getId())) {
            return true;
        }
        ISubscribeHandler subscribeHandler = SubscribeHandlerFactory.getHandler(subVo.getClassName());
        if (subscribeHandler == null) {
            throw new SubscribeHandlerNotFoundException(subVo.getClassName());
        }
        String tenantUuid = TenantContext.get().getTenantUuid();
        String topicName = subVo.getTopicName();
        String destinationName = buildTopicName(tenantUuid, topicName);
        ActiveMQConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            connectionFactory = createConnectionFactory();
            connection = connectionFactory.createConnection();
            boolean sharedConsumerSupported = isSharedConsumerSupported();
            if (isDurable(subVo) && !sharedConsumerSupported) {
                connection.setClientID(buildClientId(tenantUuid, subVo));
            }
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(destinationName);
            String subscriptionName = buildSubscriptionName(tenantUuid, subVo);
            consumer = createConsumer(session, topic, subscriptionName, subVo, sharedConsumerSupported);
            subVo.setTenantUuid(tenantUuid);
            consumer.setMessageListener(message -> {
                try {
                    subscribeHandler.onMessage(subVo, message);
                } catch (Exception ex) {
                    logger.error("消费 Artemis Topic 消息失败: {}", ex.getMessage(), ex);
                }
            });
            consumerMap.put(subVo.getId(), new ArtemisTopicConsumerHolder(connectionFactory, connection, session, consumer));
            return true;
        } catch (Exception ex) {
            JmsUtils.closeMessageConsumer(consumer);
            JmsUtils.closeSession(session);
            JmsUtils.closeConnection(connection);
            closeConnectionFactory(connectionFactory);
            logger.error(ex.getMessage(), ex);
            throw new SubscribeTopicException(destinationName, subVo.getName(), ex.getMessage());
        }
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
        ArtemisTopicConsumerHolder holder = consumerMap.remove(subscribeVo.getId());
        if (holder != null) {
            holder.close();
        }
    }

    @Override
    public void send(String topicName, String content) {
        String destinationName = buildTopicName(TenantContext.get().getTenantUuid(), topicName);
        ActiveMQConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connectionFactory = createConnectionFactory();
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(destinationName);
            producer = session.createProducer(topic);
            TextMessage message = session.createTextMessage(content);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(message);
        } catch (Exception ex) {
            logger.error("发送消息到 Artemis Topic {} 失败，异常：{}", destinationName, ex.getMessage(), ex);
        } finally {
            JmsUtils.closeMessageProducer(producer);
            JmsUtils.closeSession(session);
            JmsUtils.closeConnection(connection);
            closeConnectionFactory(connectionFactory);
        }
    }

    @Override
    public boolean isEnable() {
        return StringUtils.isNotBlank(Config.JMS_URL());
    }

    @Override
    public List<HealthcheckResultVo> healthCheck(SubscribeVo subVo) {
        List<HealthcheckResultVo> resultList = new ArrayList<>();
        String destinationName = buildTopicName(TenantContext.get().getTenantUuid(), subVo.getTopicName());
        ActiveMQConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        try {
            connectionFactory = createConnectionFactory();
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            session.createTopic(destinationName);
            resultList.add(new HealthcheckResultVo("ActiveMQ Artemis Topic连接正常", "normal"));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            resultList.add(new HealthcheckResultVo("健康检查失败，异常：" + ex.getMessage(), "error"));
        } finally {
            JmsUtils.closeSession(session);
            JmsUtils.closeConnection(connection);
            closeConnectionFactory(connectionFactory);
        }
        return resultList;
    }

    private ActiveMQConnectionFactory createConnectionFactory() {
        if (StringUtils.isNotBlank(Config.JMS_USER())) {
            return new ActiveMQConnectionFactory(Config.JMS_URL(), Config.JMS_USER(), Config.JMS_PASSWORD());
        }
        return new ActiveMQConnectionFactory(Config.JMS_URL());
    }

    private static String buildTopicName(String tenantUuid, String topicName) {
        if (StringUtils.isBlank(tenantUuid)) {
            throw new IllegalArgumentException("租户UUID不能为空，无法构建 Artemis Topic");
        }
        if (StringUtils.isBlank(topicName)) {
            throw new IllegalArgumentException("Topic名称不能为空");
        }
        return tenantUuid + "/" + topicName;
    }

    private static MessageConsumer createConsumer(Session session, Topic topic, String subscriptionName, SubscribeVo subVo, boolean sharedConsumerSupported)
            throws JMSException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (isDurable(subVo)) {
            if (sharedConsumerSupported) {
                Method method = Session.class.getMethod("createSharedDurableConsumer", Topic.class, String.class);
                return (MessageConsumer) method.invoke(session, topic, subscriptionName);
            }
            return session.createDurableSubscriber(topic, subscriptionName);
        }
        if (sharedConsumerSupported) {
            Method method = Session.class.getMethod("createSharedConsumer", Topic.class, String.class);
            return (MessageConsumer) method.invoke(session, topic, subscriptionName);
        }
        return session.createConsumer(topic);
    }

    private static boolean isSharedConsumerSupported() {
        try {
            Session.class.getMethod("createSharedDurableConsumer", Topic.class, String.class);
            Session.class.getMethod("createSharedConsumer", Topic.class, String.class);
            return true;
        } catch (NoSuchMethodException ex) {
            return false;
        }
    }

    private static String buildClientId(String tenantUuid, SubscribeVo subVo) {
        return (tenantUuid + "_" + subVo.getId()).replaceAll("[^a-zA-Z0-9_\\-.]", "_");
    }

    private static String buildSubscriptionName(String tenantUuid, SubscribeVo subVo) {
        String name = tenantUuid + "_" + subVo.getId();
        return name.replaceAll("[^a-zA-Z0-9_\\-.]", "_");
    }

    private static Boolean isDurable(SubscribeVo subVo) {
        return subVo != null && Integer.valueOf(1).equals(subVo.getIsDurable());
    }

    private static void closeConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        if (connectionFactory == null) {
            return;
        }
        try {
            connectionFactory.close();
        } catch (Exception ex) {
            logger.error("关闭 Artemis Topic 连接工厂失败: {}", ex.getMessage(), ex);
        }
    }

    private static final class ArtemisTopicConsumerHolder implements AutoCloseable {
        private final ActiveMQConnectionFactory connectionFactory;
        private final Connection connection;
        private final Session session;
        private final MessageConsumer consumer;

        private ArtemisTopicConsumerHolder(ActiveMQConnectionFactory connectionFactory, Connection connection, Session session, MessageConsumer consumer) {
            this.connectionFactory = connectionFactory;
            this.connection = connection;
            this.session = session;
            this.consumer = consumer;
        }

        @Override
        public void close() {
            JmsUtils.closeMessageConsumer(consumer);
            JmsUtils.closeSession(session);
            JmsUtils.closeConnection(connection);
            closeConnectionFactory(connectionFactory);
        }
    }
}
