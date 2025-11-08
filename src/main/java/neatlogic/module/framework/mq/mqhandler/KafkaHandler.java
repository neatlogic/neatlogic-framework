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
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        if (!containerMap.containsKey(subVo.getId())) {
            ISubscribeHandler subscribeHandler = SubscribeHandlerFactory.getHandler(subVo.getClassName());
            if (subscribeHandler == null) {
                throw new SubscribeHandlerNotFoundException(subVo.getClassName());
            }
            //kafka对主题大小写敏感，因此需要保持主题大小写！！
            String topicName = subVo.getTopicName();
            String clientName = subVo.getName();
            clientName = clientName.toLowerCase();
            String tenantUuid = TenantContext.get().getTenantUuid();


            //用租户uuid+订阅id作为分组id，确保每个消费者都可以独立消费
            consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, tenantUuid + "_" + subVo.getId());
            //客户端id仅用于标识客户端实例，每个租户共用一个
            consumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, tenantUuid + "_" + Config.SCHEDULE_SERVER_ID);
            try (AdminClient adminClient = AdminClient.create(consumerProps)) {
                ListTopicsResult topics = adminClient.listTopics();
                boolean topicExists = topics.names().get().contains(topicName);

                // 如果主题不存在，创建新主题
                if (!topicExists) {
                    NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
                    adminClient.createTopics(Collections.singleton(newTopic)).all().get();
                }
                DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
                ContainerProperties containerProperties = new ContainerProperties(topicName);
                subVo.setTenantUuid(TenantContext.get().getTenantUuid());
                containerProperties.setMessageListener((AcknowledgingMessageListener<String, String>) (consumerRecord, acknowledgment) -> {
                    try {
                        subscribeHandler.onMessage(subVo, consumerRecord.value());
                        if (acknowledgment != null) {
                            //手动提交偏移量，如果处理有问题可以重新消费
                            acknowledgment.acknowledge();
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                });

                ConcurrentMessageListenerContainer<String, String> container =
                        new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
                container.setConcurrency(1);
                container.setAutoStartup(true);
                containerMap.put(subVo.getId(), container);

                try {
                    container.start();
                } catch (Exception ex) {
                    throw new SubscribeTopicException(topicName, clientName, ex.getMessage());
                }
            }
        }

        return true;
    }

    @Override
    public void reconnect(SubscribeVo subscribeVo) throws SubscribeTopicException, ExecutionException, InterruptedException {
        MessageListenerContainer container = containerMap.get(subscribeVo.getId());
        if (container != null) {
            if (!container.isRunning()) {
                container.destroy();
                containerMap.remove(subscribeVo.getId());
                this.create(subscribeVo);
            }
        }
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
            if (!container.isRunning()) {
                container.stop();
            }
            container.destroy();
            containerMap.remove(subscribeVo.getId());
        }
    }

    @Override
    public void send(String topicName, String content) {
        //topicName = (TenantContext.get().getTenantUuid() + "_" + topicName).toLowerCase();
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, content);
            producer.send(producerRecord);
        } catch (Exception ex) {
            logger.error("发送消息到Kafka失败，异常：{}", ex.getMessage());
        }
    }

    @Override
    public boolean isEnable() {
        return StringUtils.isNotBlank(Config.KAFKA_URL());
    }


    @Override
    public List<HealthcheckResultVo> healthCheck(SubscribeVo subVo) {
        List<HealthcheckResultVo> errorList = new ArrayList<>();
        String topicName = subVo.getTopicName();
        String groupId = TenantContext.get().getTenantUuid() + "_" + subVo.getId();
        try (AdminClient adminClient = AdminClient.create(consumerProps);
             KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            // 1. 检查 topic 是否存在
            ListTopicsResult topics = adminClient.listTopics();
            boolean topicExists = topics.names().get().contains(topicName);
            if (!topicExists) {
                errorList.add(new HealthcheckResultVo("主题 " + topicName + " 不存在", "error"));
            }

            // 2. 检查 container 是否正常
            MessageListenerContainer container = containerMap.get(subVo.getId());
            if (container == null || !container.isRunning()) {
                errorList.add(new HealthcheckResultVo("订阅未激活", "warning"));
            }
            // 检查 lag 是否正常
            List<PartitionInfo> partitions = consumer.partitionsFor(topicName);
            List<TopicPartition> topicPartitions = partitions.stream()
                    .map(p -> new TopicPartition(topicName, p.partition()))
                    .collect(Collectors.toList());

            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);

            Map<TopicPartition, OffsetAndMetadata> committedOffsets =
                    adminClient.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get(10, TimeUnit.SECONDS);

            for (TopicPartition tp : topicPartitions) {
                long end = endOffsets.getOrDefault(tp, 0L);
                long committed = committedOffsets.getOrDefault(tp, new OffsetAndMetadata(0L)).offset();
                long lag = end - committed;
                if (lag > 1000) {
                    errorList.add(new HealthcheckResultVo("分区 " + tp.partition() + " 消息消费严重滞后，滞后消息 " + lag + " 条", "error"));
                } else if (lag > 0) {
                    errorList.add(new HealthcheckResultVo("分区 " + tp.partition() + " 消息消费存在滞后，滞后消息 " + lag + " 条", "warning"));
                } else {
                    errorList.add(new HealthcheckResultVo("分区 " + tp.partition() + " 无消费滞后消息", "normal"));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            errorList.add(new HealthcheckResultVo("健康检查失败，异常：" + e.getMessage(), "error"));
        }
        return errorList;
    }
}