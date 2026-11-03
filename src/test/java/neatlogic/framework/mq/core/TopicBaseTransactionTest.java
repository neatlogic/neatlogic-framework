package neatlogic.framework.mq.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.thread.ModuleInitApplicationListener;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.mq.dao.mapper.MqTopicMapper;
import neatlogic.framework.mq.dto.TopicVo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/** 使用真实主题发送链和事务同步，只替换消息代理、Mapper 及工厂注册数据。 */
public class TopicBaseTransactionTest {
    private static final String HANDLER = "TOPIC_TRANSACTION_TEST";
    private MqTopicMapper originalMapper;
    private IMqHandler originalHandler;
    private Map<String, IMqHandler> handlers;
    private final AtomicInteger lookups = new AtomicInteger();
    private final CountDownLatch sent = new CountDownLatch(1);
    private volatile String wire;
    private volatile String key;
    private volatile String tenant;

    /** 隔离测试注册项，解除正常启动时由模块加载完成释放的线程屏障。 */
    @Before @SuppressWarnings("unchecked") public void setUp() throws Exception {
        TenantContext.init("TopicTenant");
        if (ModuleInitApplicationListener.getModuleinitphaser().getPhase() == 0) {
            ModuleInitApplicationListener.getModuleinitphaser().register();
            ModuleInitApplicationListener.getModuleinitphaser().arriveAndDeregister();
        }
        originalMapper = TopicBase.mqTopicMapper;
        TopicVo topic = new TopicVo();
        topic.setName("transactiontest");
        topic.setHandler(HANDLER);
        topic.setIsActive(1);
        TopicBase.mqTopicMapper = (MqTopicMapper) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{MqTopicMapper.class}, (proxy, method, args) -> {
            if (method.getName().equals("getTopicByName")) { lookups.incrementAndGet(); return topic; }
            throw new UnsupportedOperationException(method.getName());
        });
        Field field = MqHandlerFactory.class.getDeclaredField("componentMap");
        field.setAccessible(true);
        handlers = (Map<String, IMqHandler>) field.get(null);
        originalHandler = handlers.put(HANDLER, (IMqHandler) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{IMqHandler.class}, (proxy, method, args) -> {
            if (method.getName().equals("isEnable")) { return true; }
            if (method.getName().equals("send")) {
                assertEquals("transactiontest", args[0]);
                if (args.length == 3) { key = (String) args[1]; wire = (String) args[2]; }
                else { wire = (String) args[1]; }
                tenant = TenantContext.get().getTenantUuid();
                sent.countDown();
                return null;
            }
            throw new UnsupportedOperationException(method.getName());
        }));
    }

    /** 所有异步断言完成后恢复工厂及 Mapper，避免污染其他测试。 */
    @After public void tearDown() {
        TopicBase.mqTopicMapper = originalMapper;
        if (originalHandler == null) { handlers.remove(HANDLER); }
        else { handlers.put(HANDLER, originalHandler); }
        TenantContext.get().release();
    }

    /** 真实主题链只在提交后执行，并传递业务键、租户和实际序列化正文。 */
    @Test public void commitPublishesKeyedWireAfterTransaction() throws Exception {
        JSONObject message = message();
        TransactionTemplate tx = new TransactionTemplate(new TestTransactionManager());
        tx.executeWithoutResult(status -> {
            new TestTopic(true, false).send(message);
            assertEquals(0, lookups.get());
            assertEquals(1L, sent.getCount());
        });
        assertTrue("提交后必须发送", sent.await(5, TimeUnit.SECONDS));
        assertEquals(message.getString("alert_uuid"), key);
        assertEquals("TopicTenant", tenant);
        JSONObject parsed = JSON.parseObject(wire);
        assertEquals(message.getString("alert_uuid"), parsed.getString("alert_uuid"));
        assertEquals("informant", parsed.getString("source"));
        assertEquals("TopicTenant", parsed.getString("tenant_uuid"));
        assertNull(parsed.getLong("cientity_id"));
        assertFalse("默认 JSON 序列化省略空配置项字段", parsed.containsKey("cientity_id"));
    }

    /** 回滚不能进入主题读取和发送链。 */
    @Test public void rollbackNeverPublishes() throws Exception {
        new TransactionTemplate(new TestTransactionManager()).executeWithoutResult(status -> {
            new TestTopic(true, false).send(message());
            status.setRollbackOnly();
        });
        assertFalse(sent.await(200, TimeUnit.MILLISECONDS));
        assertEquals(0, lookups.get());
    }

    /** 未覆盖分区键时沿用默认空键，无事务时直接异步发送。 */
    @Test public void defaultKeyIsNullWithoutTransaction() throws Exception {
        new TestTopic(false, false).send(message());
        assertTrue(sent.await(5, TimeUnit.SECONDS));
        assertNull(key);
    }

    /** 主题内容过滤返回空时不调用消息代理。 */
    @Test public void filteredMessageIsNotSent() throws Exception {
        TestTopic topic = new TestTopic(true, true);
        topic.send(message());
        assertTrue(topic.generated.await(5, TimeUnit.SECONDS));
        assertFalse(sent.await(200, TimeUnit.MILLISECONDS));
    }

    /** 创建保留新协议标识、但没有配置项关联的消息。 */
    private JSONObject message() {
        JSONObject message = new JSONObject();
        message.put("alert_uuid", "0123456789abcdef0123456789abcdef");
        message.put("source", "informant");
        message.put("tenant_uuid", "TopicTenant");
        message.put("cientity_id", null);
        return message;
    }

    /** 使用真实 TopicBase 模板，只有业务键和内容生成由测试主题定义。 */
    private static class TestTopic extends TopicBase<JSONObject> {
        private final boolean keyed;
        private final boolean filtered;
        private final CountDownLatch generated = new CountDownLatch(1);
        private TestTopic(boolean keyed, boolean filtered) { this.keyed = keyed; this.filtered = filtered; }
        public String getName() { return "transactiontest"; }
        public String getLabel() { return "事务测试"; }
        public String getDescription() { return "主题事务测试"; }
        public String getHandler() { return HANDLER; }
        public Boolean hasConfig() { return false; }
        /** 有业务键时覆盖分区键，否则验证基类默认逻辑。 */
        @Override protected String getMessageKey(TopicVo topic, JSONObject content) {
            if (keyed) { return content.getString("alert_uuid"); }
            return super.getMessageKey(topic, content);
        }
        /** 模拟主题过滤逻辑，不替换真实发送模板。 */
        protected JSONObject generateTopicContent(TopicVo topic, JSONObject content) {
            generated.countDown();
            if (filtered) { return null; }
            return content;
        }
    }

    /** 用真实 Spring 事务生命周期驱动提交与回滚，不连接数据库。 */
    private static class TestTransactionManager extends AbstractPlatformTransactionManager {
        private static final long serialVersionUID = 1L;
        protected Object doGetTransaction() { return new Object(); }
        protected void doBegin(Object transaction, TransactionDefinition definition) { }
        protected void doCommit(DefaultTransactionStatus status) { }
        protected void doRollback(DefaultTransactionStatus status) { }
    }
}
