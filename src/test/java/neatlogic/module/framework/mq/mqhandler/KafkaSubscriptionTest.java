package neatlogic.module.framework.mq.mqhandler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.mq.core.ISubscribeHandler;
import neatlogic.framework.mq.dto.SubscribeVo;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/** 验证订阅配置隔离及业务完成后确认，不建立 Kafka 连接。 */
public class KafkaSubscriptionTest {
    /** 初始化测试租户。 */
    @Before public void initTenant() { TenantContext.init("KafkaSubscriptionTest"); }
    /** 清理测试租户。 */
    @After public void clearTenant() { TenantContext.get().release(); }

    /** 不同订阅分组独立，同一订阅重建沿用分组，且不污染公共模板。 */
    @Test public void subscriptionPropertiesAreIndependent() {
        KafkaHandler handler = new KafkaHandler();
        SubscribeVo first = subscription(101L);
        SubscribeVo second = subscription(102L);
        Map<String, Object> firstProps = handler.createConsumerProperties(first);
        Map<String, Object> secondProps = handler.createConsumerProperties(second);
        assertEquals("KafkaSubscriptionTest_101", firstProps.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals("KafkaSubscriptionTest_102", secondProps.get(ConsumerConfig.GROUP_ID_CONFIG));
        firstProps.put(ConsumerConfig.GROUP_ID_CONFIG, "changed");
        assertEquals("KafkaSubscriptionTest_102", secondProps.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals("KafkaSubscriptionTest_101", handler.createConsumerProperties(first).get(ConsumerConfig.GROUP_ID_CONFIG));
        assertFalse(handler.consumerProps.containsKey(ConsumerConfig.GROUP_ID_CONFIG));
        assertFalse(handler.consumerProps.containsKey(ConsumerConfig.CLIENT_ID_CONFIG));
        assertFalse(firstProps.containsKey(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
    }

    /** 成功才确认，失败必须传递给容器，不能误提交失败消息。 */
    @Test @SuppressWarnings("unchecked") public void listenerAcknowledgesOnlyAfterSuccess() {
        KafkaHandler handler = new KafkaHandler();
        handler.consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:1");
        SubscribeVo sub = subscription(103L);
        AtomicInteger acknowledgments = new AtomicInteger();
        AtomicInteger calls = new AtomicInteger();
        ISubscribeHandler subscriber = new ISubscribeHandler() {
            public String getName() { return "TEST"; }
            public String getLabel() { return "测试"; }
            public void onMessage(SubscribeVo ignored, Object message) {
                assertEquals(0, acknowledgments.get());
                if (calls.incrementAndGet() == 1) { throw new IllegalStateException("暂时失败"); }
            }
        };
        ConcurrentMessageListenerContainer<String, String> container = handler.createListenerContainer(sub, subscriber, handler.createConsumerProperties(sub));
        assertEquals(ContainerProperties.AckMode.MANUAL_IMMEDIATE, container.getContainerProperties().getAckMode());
        AcknowledgingMessageListener<String, String> listener = (AcknowledgingMessageListener<String, String>) container.getContainerProperties().getMessageListener();
        ConsumerRecord<String, String> record = new ConsumerRecord<>("informant", 0, 0, "uuid", "正文");
        try {
            listener.onMessage(record, acknowledgments::incrementAndGet);
            fail("失败应传播");
        } catch (IllegalStateException expected) { assertEquals("暂时失败", expected.getMessage()); }
        assertEquals(0, acknowledgments.get());
        listener.onMessage(record, acknowledgments::incrementAndGet);
        assertEquals(1, acknowledgments.get());
    }

    /** 创建只用于本测试的订阅描述。 */
    private SubscribeVo subscription(Long id) {
        SubscribeVo sub = new SubscribeVo();
        sub.setId(id);
        sub.setTopicName("informant");
        return sub;
    }
}
