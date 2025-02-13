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
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaHandler implements IMqHandler {
    private static final Logger logger = LoggerFactory.getLogger(KafkaHandler.class);
    private static final Map<Long, MessageListenerContainer> containerMap = new ConcurrentHashMap<>();
    public static final String SEPARATOR = "#";
    Map<String, Object> consumerProps = new HashMap<>();
    Map<String, Object> producerProps = new HashMap<>();

    public KafkaHandler() {
        // Kafka consumer properties
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Config.KAFKA_URL());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 推荐手动提交以精确控制消费偏移量

        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Config.KAFKA_URL());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }


    @Override
    public String getName() {
        return "kafka";
    }

    @Override
    public String getLabel() {
        return "Apache Kafka";
    }

    @Override
    public boolean create(SubscribeVo subVo) throws SubscribeTopicException, ExecutionException, InterruptedException {
        ISubscribeHandler subscribeHandler = SubscribeHandlerFactory.getHandler(subVo.getClassName());
        if (subscribeHandler == null) {
            throw new SubscribeHandlerNotFoundException(subVo.getClassName());
        }
        String topicName = subVo.getTopicName();
        String clientName = subVo.getName();
        topicName = (TenantContext.get().getTenantUuid() + "_" + topicName).toLowerCase();
        clientName = clientName.toLowerCase();
        String tenantUuid = TenantContext.get().getTenantUuid();

        if (!containerMap.containsKey(subVo.getId())) {
            //用租户uuid+订阅id作为分组id，确保每个消费者都可以独立消费
            consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, tenantUuid + "_" + subVo.getId());
            AdminClient adminClient = AdminClient.create(consumerProps);
            ListTopicsResult topics = adminClient.listTopics();
            boolean topicExists = topics.names().get().contains(topicName);

            // 如果主题不存在，创建新主题
            if (!topicExists) {
                NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
                adminClient.createTopics(Collections.singleton(newTopic)).all().get();
            }
            // KafkaConsumerFactory
            DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);

            // Container properties
            ContainerProperties containerProperties = new ContainerProperties(topicName);
            subVo.setTenantUuid(TenantContext.get().getTenantUuid());
            containerProperties.setMessageListener((AcknowledgingMessageListener<String, String>) (record, acknowledgment) -> {
                try {
                    subscribeHandler.onMessage(subVo, record.value());
                    if (acknowledgment != null) {
                        //手动提交偏移量，如果处理有问题可以重新消费
                        acknowledgment.acknowledge();
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            });

            // Create ConcurrentMessageListenerContainer
            ConcurrentMessageListenerContainer<String, String> container =
                    new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);

            // Start container
            containerMap.put(subVo.getId(), container);

            try {
                container.start();
            } catch (Exception ex) {
                throw new SubscribeTopicException(topicName, clientName, ex.getMessage());
            }
        }

        return true;
    }

    @Override
    public void reconnect(SubscribeVo subscribeVo) throws SubscribeTopicException, ExecutionException, InterruptedException {
        this.destroy(subscribeVo);
        this.create(subscribeVo);
    }

    @Override
    public boolean isRunning(SubscribeVo subscribeVo) {
        MessageListenerContainer container = containerMap.get(subscribeVo.getId());
        return container != null && container.isRunning();
    }

    @Override
    public void destroy(SubscribeVo subscribeVo) {
        MessageListenerContainer container = containerMap.get(subscribeVo.getId());
        if (container != null) {
            if (container.isRunning()) {
                container.stop();
            }
            container.destroy();
            containerMap.remove(subscribeVo.getId());
        }
    }

    @Override
    public void send(String topicName, String content) {
        topicName = (TenantContext.get().getTenantUuid() + "_" + topicName).toLowerCase();
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, content);
            producer.send(record);
        } catch (Exception ex) {
            logger.error("发送消息到Kafka失败，异常：{}", ex.getMessage());
        }
    }

    @Override
    public boolean isEnable() {
        return StringUtils.isNotBlank(Config.KAFKA_URL());
    }
}