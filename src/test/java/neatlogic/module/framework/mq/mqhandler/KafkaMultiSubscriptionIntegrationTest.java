package neatlogic.module.framework.mq.mqhandler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.mq.core.ISubscribeHandler;
import neatlogic.framework.mq.dto.SubscribeVo;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** 显式指定 kafka.integration.bootstrap 后，使用隔离主题验证真实代理上的独立订阅。 */
public class KafkaMultiSubscriptionIntegrationTest {
    /** 多订阅完整消费，第三订阅不抢消息，失败订阅不提前提交且不阻塞其他订阅。 */
    @Test public void independentSubscriptionsAndFailureRecovery() throws Exception {
        String bootstrap = System.getProperty("kafka.integration.bootstrap");
        Assume.assumeTrue("未配置真实 Kafka 代理", bootstrap != null && !bootstrap.trim().isEmpty());
        String suffix = UUID.randomUUID().toString().replace("-", "");
        String topic = "neatlogic-subscription-test-" + suffix;
        String tenant = "test" + suffix;
        Map<String, Object> adminProps = new HashMap<>();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        List<ConcurrentMessageListenerContainer<String, String>> containers = new ArrayList<>();
        TenantContext.init(tenant);
        try (AdminClient admin = AdminClient.create(adminProps)) {
            admin.createTopics(Collections.singletonList(new NewTopic(topic, 3, (short) 1))).all().get(30, TimeUnit.SECONDS);
            try {
                KafkaHandler handler = new KafkaHandler();
                handler.consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
                AtomicBoolean allowRecovery = new AtomicBoolean(false);
                AtomicInteger failedCalls = new AtomicInteger();
                List<String> alertMessages = new CopyOnWriteArrayList<>();
                List<String> cmdbMessages = new CopyOnWriteArrayList<>();
                List<String> thirdMessages = new CopyOnWriteArrayList<>();
                SubscribeVo alert = subscription(1L, topic);
                SubscribeVo cmdb = subscription(2L, topic);
                containers.add(start(handler, alert, subscriber(alertMessages, null, null)));
                containers.add(start(handler, cmdb, subscriber(cmdbMessages, allowRecovery, failedCalls)));
                await(() -> assigned(containers), "前两个订阅分区分配超时");
                try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties(bootstrap))) {
                    RecordMetadata first = producer.send(new ProducerRecord<>(topic, "stable-alert-uuid", "critical")).get(30, TimeUnit.SECONDS);
                    await(() -> alertMessages.size() == 1 && failedCalls.get() > 0, "首次消息消费超时");
                    long before = committed(admin, tenant + "_2", topic, first.partition());
                    assertTrue("失败消息不能提前提交", before <= first.offset());
                    containers.add(start(handler, subscription(3L, topic), subscriber(thirdMessages, null, null)));
                    await(() -> assigned(containers), "第三个订阅分区分配超时");
                    RecordMetadata second = producer.send(new ProducerRecord<>(topic, "stable-alert-uuid", "healthy")).get(30, TimeUnit.SECONDS);
                    assertEquals("相同业务键应落在同一分区", first.partition(), second.partition());
                    await(() -> alertMessages.size() == 2 && thirdMessages.size() == 1, "失败订阅不能阻塞其他消费组");
                    assertTrue(cmdbMessages.isEmpty());
                    allowRecovery.set(true);
                    await(() -> cmdbMessages.size() == 2, "失败订阅重试恢复超时");
                    await(() -> committed(admin, tenant + "_2", topic, second.partition()) > second.offset(), "成功后的偏移量提交超时");
                    assertEquals(Arrays.asList("critical", "healthy"), alertMessages);
                    assertEquals(Arrays.asList("critical", "healthy"), cmdbMessages);
                    assertEquals(Collections.singletonList("healthy"), thirdMessages);
                    assertEquals(tenant + "_1", handler.createConsumerProperties(alert).get(ConsumerConfig.GROUP_ID_CONFIG));
                    assertEquals(tenant + "_2", handler.createConsumerProperties(cmdb).get(ConsumerConfig.GROUP_ID_CONFIG));
                }
            } finally {
                for (ConcurrentMessageListenerContainer<String, String> container : containers) { container.stop(); }
                admin.deleteTopics(Collections.singletonList(topic)).all().get(30, TimeUnit.SECONDS);
            }
        } finally { TenantContext.get().release(); }
    }

    /** 使用生产容器工厂，保持首次从最新位置消费的规则。 */
    private ConcurrentMessageListenerContainer<String, String> start(KafkaHandler handler, SubscribeVo sub, ISubscribeHandler listener) {
        Map<String, Object> props = handler.createConsumerProperties(sub);
        props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, 1000);
        ConcurrentMessageListenerContainer<String, String> container = handler.createListenerContainer(sub, listener, props);
        container.start();
        return container;
    }

    /** 模拟正常处理和临时数据库故障。 */
    private ISubscribeHandler subscriber(List<String> received, AtomicBoolean allow, AtomicInteger attempts) {
        return new ISubscribeHandler() {
            public String getName() { return "TEST"; }
            public String getLabel() { return "隔离测试"; }
            public void onMessage(SubscribeVo ignored, Object message) {
                if (allow != null && !allow.get()) {
                    attempts.incrementAndGet();
                    throw new IllegalStateException("模拟临时数据库故障");
                }
                received.add(message.toString());
            }
        };
    }

    /** 创建独立测试订阅。 */
    private SubscribeVo subscription(Long id, String topic) {
        SubscribeVo sub = new SubscribeVo(); sub.setId(id); sub.setTopicName(topic); return sub;
    }

    /** 配置真实测试发送器。 */
    private Map<String, Object> producerProperties(String bootstrap) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        return props;
    }

    /** 各独立消费组都应分配到主题全部分区。 */
    private boolean assigned(List<ConcurrentMessageListenerContainer<String, String>> containers) {
        for (ConcurrentMessageListenerContainer<String, String> container : containers) {
            if (container.getAssignedPartitions() == null || container.getAssignedPartitions().size() != 3) { return false; }
        }
        return true;
    }

    /** 读取目标订阅的提交进度。 */
    private long committed(AdminClient admin, String group, String topic, int partition) {
        try {
            OffsetAndMetadata offset = admin.listConsumerGroupOffsets(group).partitionsToOffsetAndMetadata().get(5, TimeUnit.SECONDS).get(new TopicPartition(topic, partition));
            if (offset != null) { return offset.offset(); }
            return -1;
        } catch (Exception ex) { return -1; }
    }

    /** 有界等待异步消费和代理状态。 */
    private void await(BooleanSupplier condition, String message) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(45);
        while (!condition.getAsBoolean() && System.nanoTime() < deadline) { Thread.sleep(100); }
        assertTrue(message, condition.getAsBoolean());
    }
}
