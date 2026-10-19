package neatlogic.module.framework.mq.mqhandler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.exception.mq.MqSendUnconfirmedException;
import neatlogic.framework.mq.core.IMqHandler;
import neatlogic.framework.mq.dto.HealthcheckResultVo;
import neatlogic.framework.mq.dto.MqSendResultVo;
import neatlogic.framework.mq.dto.SubscribeVo;
import org.apache.activemq.artemis.api.core.ActiveMQConnectionTimedOutException;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.*;
import java.lang.IllegalStateException;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/** 使用内存发送器和 JMS 接口替身验证回执，不建立任何消息代理连接。 */
public class MqSendResultTest {
    /** 建立隔离租户上下文。 */
    @Before
    public void initTenant() { TenantContext.init("TenantA"); }

    /** 清理中断标志和线程上下文。 */
    @After
    public void releaseTenant() {
        Thread.interrupted();
        TenantContext.get().release();
    }

    /** 验证代理回执、主题大小写和严格发送配置。 */
    @Test
    public void kafkaReturnsBrokerMetadataAndPreservesCase() throws Exception {
        KafkaStub handler = new KafkaStub();
        handler.future = CompletableFuture.completedFuture(new RecordMetadata(new TopicPartition("Mixed.Topic", 2), 13L, 0, 0L, 0, 10));
        MqSendResultVo result = handler.sendWithResult("Mixed.Topic", "{\"name\":\"配置项\"}");
        assertEquals("kafka", result.getHandler());
        assertEquals("Mixed.Topic", result.getDestination());
        assertEquals(Integer.valueOf(2), result.getPartition());
        assertEquals(Long.valueOf(13), result.getOffset());
        assertNull(result.getMessageId());
        assertEquals("Mixed.Topic", handler.record.topic());
        assertEquals("{\"name\":\"配置项\"}", handler.record.value());
        assertEquals("all", handler.properties.get(ProducerConfig.ACKS_CONFIG));
        assertEquals(30000L, handler.properties.get(ProducerConfig.MAX_BLOCK_MS_CONFIG));
        assertEquals(0, handler.properties.get(ProducerConfig.RETRIES_CONFIG));
        assertEquals(Duration.ofSeconds(5), handler.closeDuration);
        assertFalse(handler.producerProps.containsKey(ProducerConfig.ACKS_CONFIG));
    }

    /** 验证等待超时不误报成功并关闭发送器。 */
    @Test
    public void kafkaTimeoutIsUnconfirmedAndClosesProducer() throws Exception {
        KafkaStub handler = new KafkaStub();
        handler.future = failingFuture(new TimeoutException("没有回执"));
        try {
            handler.sendWithResult("CaseSensitive", "正文");
            fail("应保留未确认状态");
        } catch (MqSendUnconfirmedException ex) {
            assertEquals("kafka", ex.getHandler());
            assertEquals("CaseSensitive", ex.getDestination());
        }
        assertEquals(Duration.ofSeconds(5), handler.closeDuration);
        assertEquals(30L, handler.waitTimeout);
        assertEquals(TimeUnit.SECONDS, handler.waitUnit);
    }

    /** 验证异步发送失败中的超时归类。 */
    @Test
    public void kafkaFutureDeliveryTimeoutIsUnconfirmed() throws Exception {
        KafkaStub handler = new KafkaStub();
        CompletableFuture<RecordMetadata> future = new CompletableFuture<>();
        future.completeExceptionally(new org.apache.kafka.common.errors.TimeoutException("超时"));
        handler.future = future;
        try {
            handler.sendWithResult("topic", "正文");
            fail("异步发送超时必须传出");
        } catch (MqSendUnconfirmedException ex) {
            assertTrue(ex.getCause() instanceof org.apache.kafka.common.errors.TimeoutException);
        }
        assertTrue(handler.closed);
    }

    /** 验证关闭资源后仍保留线程中断标志。 */
    @Test
    public void kafkaInterruptedWaitRestoresThreadFlag() throws Exception {
        KafkaStub handler = new KafkaStub();
        handler.future = failingFuture(new InterruptedException("被中断"));
        handler.clearInterruptOnClose = true;
        try {
            handler.sendWithResult("topic", "正文");
            fail("等待中断不能当成成功");
        } catch (MqSendUnconfirmedException ex) {
            assertTrue(Thread.currentThread().isInterrupted());
        }
        assertTrue(handler.closed);
    }

    /** 验证明确发送错误不被关闭错误掩盖。 */
    @Test
    public void kafkaExplicitFailureIsNotUnconfirmedAndCloseCannotMaskIt() throws Exception {
        KafkaStub handler = new KafkaStub();
        handler.sendFailure = new IllegalArgumentException("消息参数错误");
        handler.closeFailure = true;
        try {
            handler.sendWithResult("topic", "正文");
            fail("明确发送失败应传播");
        } catch (IllegalArgumentException ex) {
            assertEquals("消息参数错误", ex.getMessage());
        }
        assertTrue(handler.closed);
    }

    /** 验证旧接口继续使用原配置并容忍发送异常。 */
    @Test
    public void kafkaLegacySendRetainsProducerConfigurationAndLogTolerance() {
        KafkaStub handler = new KafkaStub();
        handler.producerProps.put(ProducerConfig.RETRIES_CONFIG, 5);
        handler.producerProps.put(ProducerConfig.ACKS_CONFIG, "1");
        handler.sendFailure = new IllegalArgumentException("旧调用不抛出");
        handler.send("Mixed.Topic", "正文");
        assertEquals(5, handler.properties.get(ProducerConfig.RETRIES_CONFIG));
        assertEquals("1", handler.properties.get(ProducerConfig.ACKS_CONFIG));
        assertFalse(handler.properties.containsKey(ProducerConfig.MAX_BLOCK_MS_CONFIG));
        assertNull(handler.closeDuration);
        assertTrue(handler.closed);
    }

    /** 验证参数错误时不创建外部资源。 */
    @Test
    public void kafkaRejectsEmptyDestinationBeforeCreatingProducer() throws Exception {
        KafkaStub handler = new KafkaStub();
        try {
            handler.sendWithResult(" ", "正文");
            fail("空目标应被拒绝");
        } catch (IllegalArgumentException expected) {
            assertNull(handler.properties);
        }
    }

    /** 验证队列命名、持久化、确认配置与资源关闭。 */
    @Test
    public void artemisQueueReturnsMessageIdAndExistingTenantNaming() throws Exception {
        JmsStub state = new JmsStub();
        ActiveMqArtemisHandler handler = queueHandler(state);
        MqSendResultVo result = handler.sendWithResult("Mixed.Queue", "正文");
        assertEquals("artemis", result.getHandler());
        assertEquals("TenantA/mixed.queue", result.getDestination());
        assertEquals("ID:broker-17", result.getMessageId());
        assertEquals("正文", state.content);
        assertEquals(DeliveryMode.PERSISTENT, state.deliveryMode);
        assertTrue(state.blockOnDurableSend);
        assertEquals(30000L, state.callTimeout);
        assertEquals(0, state.reconnectAttempts);
        assertEquals(1, state.initialConnectAttempts);
        assertEquals(3, state.closedResources.size());
        assertTrue(state.factory.closed);
    }

    /** 验证主题保留大小写及消息代理分配的消息标识。 */
    @Test
    public void artemisTopicPreservesCaseAndReturnsBrokerMessageId() throws Exception {
        JmsStub state = new JmsStub();
        MqSendResultVo result = topicHandler(state).sendWithResult("Mixed.Topic", "正文");
        assertEquals("artemis-topic", result.getHandler());
        assertEquals("TenantA/Mixed.Topic", result.getDestination());
        assertEquals("TenantA/Mixed.Topic", state.topicName);
        assertEquals("ID:broker-17", result.getMessageId());
        assertEquals(DeliveryMode.PERSISTENT, state.deliveryMode);
        assertTrue(state.connectionStarted);
        assertEquals(3, state.closedResources.size());
        assertTrue(state.factory.closed);
    }

    /** 验证两种 Artemis 发送器都识别 JMS 包装的超时。 */
    @Test
    public void artemisTimeoutLinkedExceptionIsUnconfirmedForBothTransports() throws Exception {
        for (boolean topic : new boolean[]{false, true}) {
            JmsStub state = new JmsStub();
            JMSException timeout = new JMSException("确认超时");
            timeout.setLinkedException(new ActiveMQConnectionTimedOutException("代理未确认"));
            state.sendFailure = timeout;
            IMqHandler handler = mqHandler(state, topic);
            try {
                handler.sendWithResult("Mixed.Topic", "正文");
                fail("必须报告未确认");
            } catch (MqSendUnconfirmedException ex) {
                assertEquals(handler.getName(), ex.getHandler());
                if (topic) {
                    assertEquals("TenantA/Mixed.Topic", ex.getDestination());
                } else {
                    assertEquals("TenantA/mixed.topic", ex.getDestination());
                }
            }
            assertEquals(3, state.closedResources.size());
            assertTrue(state.factory.closed);
        }
    }

    /** 验证明确拒绝继续抛出，旧接口继续容错并保留连接配置。 */
    @Test
    public void artemisExplicitFailurePropagatesAndLegacySendRemainsTolerant() throws Exception {
        for (boolean topic : new boolean[]{false, true}) {
            JmsStub state = new JmsStub();
            state.sendFailure = new JMSException("认证失败");
            try {
                mqHandler(state, topic).sendWithResult("topic", "正文");
                fail("明确拒绝必须传出");
            } catch (JMSException ex) {
                assertEquals("认证失败", ex.getMessage());
            }
            assertTrue(state.factory.closed);
            JmsStub legacy = new JmsStub();
            legacy.sendFailure = new JMSException("发送失败");
            legacy.factory.setCallTimeout(12345L);
            legacy.factory.setReconnectAttempts(4);
            mqHandler(legacy, topic).send("topic", "正文");
            assertEquals(12345L, legacy.callTimeout);
            assertEquals(4, legacy.reconnectAttempts);
            assertTrue(legacy.factory.closed);
        }
    }

    /** 验证空目标和主题缺少租户时不创建连接。 */
    @Test
    public void artemisRejectsInvalidDestinationBeforeOpeningResources() throws Exception {
        JmsStub state = new JmsStub();
        try {
            queueHandler(state).sendWithResult("", "正文");
            fail("空目标应被拒绝");
        } catch (IllegalArgumentException ex) {
            assertFalse(state.connectionCreated);
        }
        TenantContext.init();
        try {
            topicHandler(state).sendWithResult("topic", "正文");
            fail("Topic需要租户前缀");
        } catch (IllegalArgumentException ex) {
            assertFalse(state.connectionCreated);
        } finally {
            state.factory.close();
        }
    }

    /** 验证旧扩展获得默认不支持行为，不伪造成功。 */
    @Test
    public void legacyExtensionDoesNotPretendToSupportReceipts() throws Exception {
        IMqHandler legacy = new LegacyHandler();
        try {
            legacy.sendWithResult("topic", "正文");
            fail("没有回执能力的扩展不得报告成功");
        } catch (UnsupportedOperationException ex) {
            assertTrue(ex.getMessage().contains("legacy"));
        }
    }

    /** 在替身发送器中模拟阻塞回执，不实际等待超时时间。 */
    private Future<RecordMetadata> failingFuture(final Exception error) {
        return new Future<RecordMetadata>() {
            public boolean cancel(boolean mayInterruptIfRunning) { return false; }
            public boolean isCancelled() { return false; }
            public boolean isDone() { return false; }
            public RecordMetadata get() throws InterruptedException, ExecutionException { throw new ExecutionException(error); }
            public RecordMetadata get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                if (error instanceof InterruptedException) { throw (InterruptedException) error; }
                if (error instanceof TimeoutException) { throw (TimeoutException) error; }
                throw new ExecutionException(error);
            }
        };
    }

    /** 创建 Queue 处理器，替换唯一的网络连接边界。 */
    private ActiveMqArtemisHandler queueHandler(final JmsStub state) {
        return new ActiveMqArtemisHandler() {
            protected ActiveMQConnectionFactory createSendConnectionFactory() { return state.factory; }
        };
    }

    /** 创建 Topic 处理器，替换唯一的网络连接边界。 */
    private ActiveMqArtemisTopicHandler topicHandler(final JmsStub state) {
        return new ActiveMqArtemisTopicHandler() {
            protected ActiveMQConnectionFactory createSendConnectionFactory() { return state.factory; }
        };
    }

    /** 使用同一套场景检查 Queue 与 Topic 的发送和资源释放契约。 */
    private IMqHandler mqHandler(JmsStub state, boolean topic) {
        if (topic) { return topicHandler(state); }
        return queueHandler(state);
    }

    private static class KafkaStub extends KafkaHandler {
        Map<String, Object> properties;
        ProducerRecord<String, String> record;
        Future<RecordMetadata> future;
        RuntimeException sendFailure;
        boolean closeFailure;
        boolean clearInterruptOnClose;
        boolean closed;
        Duration closeDuration;
        long waitTimeout;
        TimeUnit waitUnit;

        /** 捕获真实传入的配置与记录，只替换网络客户端。 */
        @SuppressWarnings("unchecked")
        protected Producer<String, String> createProducer(Map<String, Object> properties) {
            this.properties = new HashMap<>(properties);
            return (Producer<String, String>) Proxy.newProxyInstance(Producer.class.getClassLoader(), new Class<?>[]{Producer.class}, (proxy, method, args) -> {
                if ("send".equals(method.getName())) {
                    record = (ProducerRecord<String, String>) args[0];
                    if (sendFailure != null) { throw sendFailure; }
                    return Proxy.newProxyInstance(Future.class.getClassLoader(), new Class<?>[]{Future.class}, (p, m, a) -> {
                        if ("get".equals(m.getName()) && a != null && a.length == 2) {
                            waitTimeout = (Long) a[0];
                            waitUnit = (TimeUnit) a[1];
                            try { return future.get(waitTimeout, waitUnit); }
                            catch (Exception ex) { throw ex; }
                        }
                        return null;
                    });
                }
                if ("close".equals(method.getName())) {
                    closed = true;
                    if (clearInterruptOnClose) { Thread.interrupted(); }
                    if (args != null && args.length == 1) { closeDuration = (Duration) args[0]; }
                    if (closeFailure) { throw new IllegalStateException("关闭失败"); }
                }
                return null;
            });
        }
    }

    private static class JmsStub {
        final StubConnectionFactory factory = new StubConnectionFactory(this);
        final List<String> closedResources = new ArrayList<>();
        String content;
        String topicName;
        int deliveryMode;
        JMSException sendFailure;
        boolean connectionCreated;
        boolean connectionStarted;
        boolean blockOnDurableSend;
        long callTimeout;
        int reconnectAttempts;
        int initialConnectAttempts;

        /** JMS 接口替身记录调用与关闭顺序，发送异常由测试注入。 */
        @SuppressWarnings("unchecked")
        <T> T proxy(Class<T> type) {
            return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
                switch (method.getName()) {
                    case "createSession": return proxy(Session.class);
                    case "createProducer": return proxy(MessageProducer.class);
                    case "createTextMessage": content = (String) args[0]; return proxy(TextMessage.class);
                    case "createTopic": topicName = (String) args[0]; return new ActiveMQTopic(topicName);
                    case "setDeliveryMode": deliveryMode = (Integer) args[0]; return null;
                    case "send": if (sendFailure != null) { throw sendFailure; } return null;
                    case "getJMSMessageID": return "ID:broker-17";
                    case "start": connectionStarted = true; return null;
                    case "close": closedResources.add(type.getSimpleName()); return null;
                    default: return null;
                }
            });
        }
    }

    private static class StubConnectionFactory extends ActiveMQConnectionFactory {
        private static final long serialVersionUID = 1L;
        private final transient JmsStub state;
        boolean closed;

        StubConnectionFactory(JmsStub state) {
            super("tcp://localhost:61616");
            this.state = state;
        }

        /** 不连接代理，只采样发送路径实际生效的配置。 */
        @Override
        public Connection createConnection() {
            state.connectionCreated = true;
            state.blockOnDurableSend = isBlockOnDurableSend();
            state.callTimeout = getCallTimeout();
            state.reconnectAttempts = getReconnectAttempts();
            state.initialConnectAttempts = getInitialConnectAttempts();
            return state.proxy(Connection.class);
        }

        @Override
        public void close() { closed = true; super.close(); }
    }

    /** 模拟没有实现新接口的外部扩展，验证默认方法兼容性。 */
    private static class LegacyHandler implements IMqHandler {
        public String getName() { return "legacy"; }
        public String getLabel() { return "legacy"; }
        public boolean create(SubscribeVo vo) { return false; }
        public void reconnect(SubscribeVo vo) { }
        public boolean isRunning(SubscribeVo vo) { return false; }
        public void destroy(SubscribeVo vo) { }
        public void send(String topic, String content) { }
        public boolean isEnable() { return true; }
        public List<HealthcheckResultVo> healthCheck(SubscribeVo vo) { return Collections.emptyList(); }
    }
}
