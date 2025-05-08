/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.module.framework.mq.mqhandler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.mq.SubscribeHandlerNotFoundException;
import neatlogic.framework.exception.mq.SubscribeTopicException;
import neatlogic.framework.mq.core.IMqHandler;
import neatlogic.framework.mq.core.ISubscribeHandler;
import neatlogic.framework.mq.core.SubscribeHandlerFactory;
import neatlogic.framework.mq.dto.SubscribeVo;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveMqArtemisHandler implements IMqHandler {
    private static final Logger logger = LoggerFactory.getLogger(ActiveMqArtemisHandler.class);
    private static final Map<Long, MessageConsumer> consumerMap = new ConcurrentHashMap<>();

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
            String queueName = TenantContext.get().getTenantUuid() + "/" + topicName;
            subVo.setTenantUuid(TenantContext.get().getTenantUuid());

            try {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl, user, password);
                Connection connection = connectionFactory.createConnection();
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = new ActiveMQQueue(queueName);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(message -> {
                    try {
                        subscribeHandler.onMessage(subVo, message);
                    } catch (Exception ex) {
                        logger.error("消费消息失败: {}", ex.getMessage(), ex);
                    }
                });
                consumerMap.put(subVo.getId(), consumer);
            } catch (Exception ex) {
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
        MessageConsumer consumer = consumerMap.get(subscribeVo.getId());
        if (consumer != null) {
            try {
                consumer.close();
            } catch (JMSException e) {
                logger.error("关闭消费者失败: {}", e.getMessage());
            }
            consumerMap.remove(subscribeVo.getId());
        }
    }

    @Override
    public void send(String topicName, String content) {
        String queueName = TenantContext.get().getTenantUuid() + "/" + topicName.toLowerCase();
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
}
