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
import neatlogic.framework.exception.mq.MqSendUnconfirmedException;
import neatlogic.framework.exception.mq.SubscribeHandlerNotFoundException;
import neatlogic.framework.exception.mq.SubscribeTopicException;
import neatlogic.framework.mq.core.IMqHandler;
import neatlogic.framework.mq.core.ISubscribeHandler;
import neatlogic.framework.mq.core.SubscribeHandlerFactory;
import neatlogic.framework.mq.dto.HealthcheckResultVo;
import neatlogic.framework.mq.dto.MqSendResultVo;
import neatlogic.framework.mq.dto.SubscribeVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.*;
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

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
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

    /** 保留旧调用方的发送配置和日志容错约定，复用发送及资源释放流程。 */
    @Override
    public void send(String topicName, String content) {
        try {
            sendInternal(topicName, content, false);
        } catch (Exception ex) {
            logger.error("发送消息到Kafka失败，异常：{}", ex.getMessage(), ex);
        }
    }

    /** 等待代理回执，Kafka 目标名称保持大小写，不附加租户前缀。 */
    @Override
    public MqSendResultVo sendWithResult(String topicName, String content) throws Exception {
        return sendInternal(topicName, content, true);
    }

    /** 共享发送和资源释放，严格确认路径的超时及重试配置不改变旧调用方。 */
    private MqSendResultVo sendInternal(String topicName, String content, boolean waitForResult) throws Exception {
        if (StringUtils.isBlank(topicName)) {
            throw new IllegalArgumentException("Topic名称不能为空");
        }
        Map<String, Object> properties = new HashMap<>(producerProps);
        if (waitForResult) {
            // 限制元数据等待、发送确认及关闭时间；不修改消费者和原有发送配置。
            properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30000L);
            properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
            properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 30000);
            properties.put(ProducerConfig.LINGER_MS_CONFIG, 0);
            properties.put(ProducerConfig.ACKS_CONFIG, "all");
            properties.put(ProducerConfig.RETRIES_CONFIG, 0);
            properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        }
        Producer<String, String> producer = null;
        boolean restoreInterrupt = false;
        try {
            producer = createProducer(properties);
            Future<RecordMetadata> future = producer.send(new ProducerRecord<>(topicName, content));
            if (!waitForResult) {
                return null;
            }
            RecordMetadata metadata = future.get(30, TimeUnit.SECONDS);
            if (metadata == null || metadata.offset() < 0) {
                throw new MqSendUnconfirmedException(getName(), topicName, "Kafka发送结果未确认：" + topicName, null);
            }
            MqSendResultVo result = new MqSendResultVo();
            result.setHandler(getName());
            result.setDestination(metadata.topic());
            result.setPartition(metadata.partition());
            result.setOffset(metadata.offset());
            return result;
        } catch (InterruptedException | org.apache.kafka.common.errors.InterruptException ex) {
            restoreInterrupt = true;
            Thread.currentThread().interrupt();
            throw new MqSendUnconfirmedException(getName(), topicName, "等待Kafka发送确认被中断：" + topicName, ex);
        } catch (TimeoutException | org.apache.kafka.common.errors.TimeoutException ex) {
            throw new MqSendUnconfirmedException(getName(), topicName, "等待Kafka发送确认超时：" + topicName, ex);
        } catch (ExecutionException ex) {
            if (ex.getCause() instanceof org.apache.kafka.common.errors.TimeoutException) {
                throw new MqSendUnconfirmedException(getName(), topicName, "等待Kafka发送确认超时：" + topicName, ex.getCause());
            }
            if (ex.getCause() instanceof InterruptedException || ex.getCause() instanceof org.apache.kafka.common.errors.InterruptException) {
                restoreInterrupt = true;
                Thread.currentThread().interrupt();
                throw new MqSendUnconfirmedException(getName(), topicName, "等待Kafka发送确认被中断：" + topicName, ex.getCause());
            }
            throw ex;
        } finally {
            if (producer != null) {
                try {
                    if (waitForResult) {
                        producer.close(Duration.ofSeconds(5));
                    } else {
                        producer.close();
                    }
                } catch (Exception ex) {
                    // 资源关闭不改变已经收到的代理回执，也不覆盖原发送异常。
                    logger.error("关闭Kafka发送器失败：{}", ex.getMessage(), ex);
                }
            }
            if (restoreInterrupt) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /** 创建发送客户端，测试可在此替换外部消息代理。 */
    protected Producer<String, String> createProducer(Map<String, Object> properties) {
        return new KafkaProducer<>(properties);
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