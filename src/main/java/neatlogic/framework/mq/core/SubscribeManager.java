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

package neatlogic.framework.mq.core;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.mq.SubscribeTopicException;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.lang.Nullable;

import javax.jms.*;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RootComponent
public final class SubscribeManager {
    private static final Map<String, SimpleMessageListenerContainer> containerMap = new ConcurrentHashMap<>();
    private static ConnectionFactory connectionFactory;
    public static final String SEPARATOR = "#";

    public static Map<String, SimpleMessageListenerContainer> getListenerMap() {
        return containerMap;
    }


    @Autowired
    public void setConnectionFactory(ActiveMQConnectionFactory _connectionFactory) {
        connectionFactory = _connectionFactory;
    }

    /**
     * 启动订阅主题
     *
     * @param topicName  主题名称
     * @param clientName 订阅名称
     * @return 是否成功
     */
    public static boolean start(String topicName, String clientName) {
        topicName = topicName.toLowerCase(Locale.ROOT);
        clientName = clientName.toLowerCase(Locale.ROOT);
        if (containerMap.containsKey(TenantContext.get().getTenantUuid() + SEPARATOR + topicName + SEPARATOR + clientName)) {
            SimpleMessageListenerContainer container = containerMap.get(TenantContext.get().getTenantUuid() + SEPARATOR + topicName + SEPARATOR + clientName);
            if (!container.isRunning()) {
                container.start();
            }
            return true;
        }
        return false;
    }

    /**
     * 停止订阅主题
     *
     * @param topicName  主题名称
     * @param clientName 订阅名称
     * @return 是否成功
     */
    public static boolean stop(String topicName, String clientName) {
        topicName = topicName.toLowerCase(Locale.ROOT);
        clientName = clientName.toLowerCase(Locale.ROOT);
        if (containerMap.containsKey(TenantContext.get().getTenantUuid() + SEPARATOR + topicName + SEPARATOR + clientName)) {
            SimpleMessageListenerContainer container = containerMap.get(TenantContext.get().getTenantUuid() + SEPARATOR + topicName + SEPARATOR + clientName);
            if (container.isRunning()) {
                container.stop();
            }
            return true;
        }
        return false;
    }

    /**
     * 删除订阅
     *
     * @param topicName  主题名称
     * @param clientName 订阅名称
     */
    public static void destroy(String topicName, String clientName) {
        topicName = topicName.toLowerCase(Locale.ROOT);
        clientName = clientName.toLowerCase(Locale.ROOT);
        if (containerMap.containsKey(TenantContext.get().getTenantUuid() + SEPARATOR + topicName + SEPARATOR + clientName)) {
            SimpleMessageListenerContainer container = containerMap.get(TenantContext.get().getTenantUuid() + SEPARATOR + topicName + SEPARATOR + clientName);
            if (container.isRunning()) {
                container.stop();
            }
            container.shutdown();
            container.destroy();
            containerMap.remove(TenantContext.get().getTenantUuid() + SEPARATOR + topicName + SEPARATOR + clientName);
        }
    }

    /**
     * 创建订阅
     *
     * @param topicName        主题名称
     * @param clientName       订阅名称
     * @param isDurable        是否持久订阅
     * @param subscribeHandler 订阅处理器
     * @return 是否成功
     */
    public static boolean create(String topicName, String clientName, boolean isDurable, ISubscribeHandler subscribeHandler) throws SubscribeTopicException {
        topicName = topicName.toLowerCase(Locale.ROOT);
        clientName = clientName.toLowerCase(Locale.ROOT);
        if (!containerMap.containsKey(TenantContext.get().getTenantUuid() + SEPARATOR + topicName + SEPARATOR + clientName)) {
            String finalClientName = clientName;
            String tenantUuid = TenantContext.get().getTenantUuid();
            String finalTopicName = topicName;
            MessageListenerAdapter messageAdapter = new MessageListenerAdapter() {
                @Override
                public void onMessage(Message message, @Nullable Session session) throws JMSException {
                    try {
                        subscribeHandler.onMessage((TextMessage) message, session, finalTopicName, finalClientName, tenantUuid);
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            };

            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setPubSubDomain(true);
            container.setDestinationName(TenantContext.get().getTenantUuid() + "/" + topicName);
            container.setDurableSubscriptionName(TenantContext.get().getTenantUuid() + "/" + clientName + "/" + Config.SCHEDULE_SERVER_ID);
            container.setSubscriptionDurable(isDurable);
            container.setClientId(TenantContext.get().getTenantUuid() + "/" + clientName + "/" + Config.SCHEDULE_SERVER_ID);
            container.setMessageListener(messageAdapter);
            //container.setAutoStartup(true);
            containerMap.put(TenantContext.get().getTenantUuid() + SEPARATOR + topicName + SEPARATOR + clientName, container);
            try {
                container.start();
            } catch (Exception ex) {
                throw new SubscribeTopicException(topicName, clientName, ex.getMessage());
            }
        }
        return true;
    }
}
