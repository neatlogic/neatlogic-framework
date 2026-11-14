package neatlogic.framework.dashboard.datasource.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.dashboard.datasource.exception.DashboardDatasourceTypeNotSupportedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/** 通过真实模块刷新事件验证注册、分发和并发冲突，不依赖数据库或完整 Spring 配置。 */
public class DashboardDatasourceHandlerFactoryTest {
    private final DashboardDatasourceHandlerFactory factory = new DashboardDatasourceHandlerFactory();
    private ConcurrentMap<String, IDashboardDatasourceHandler> handlers;
    private Map<String, IDashboardDatasourceHandler> originalHandlers;

    /** 隔离静态注册表并保留原对象引用，避免测试顺序影响结果。 */
    @Before
    @SuppressWarnings("unchecked")
    public void initialize() throws Exception {
        Field field = DashboardDatasourceHandlerFactory.class.getDeclaredField("handlerMap");
        field.setAccessible(true);
        handlers = (ConcurrentMap<String, IDashboardDatasourceHandler>) field.get(null);
        originalHandlers = new HashMap<>(handlers);
        handlers.clear();
    }

    /** 恢复执行测试前的全部注册项，防止污染其他模块测试。 */
    @After
    public void cleanup() {
        if (handlers != null && originalHandlers != null) {
            handlers.clear();
            handlers.putAll(originalHandlers);
        }
    }

    /** 显式指定数据仓库类型时，返回模块事件注册的 Handler。 */
    @Test
    public void shouldResolveExplicitTypeAfterModuleEvent() {
        IDashboardDatasourceHandler handler = new StubHandler("datawarehouse");
        refresh("tenant", handler);
        assertSame(handler, DashboardDatasourceHandlerFactory.getHandler("datawarehouse"));
        assertEquals(1, handlers.size());
    }

    /** 未注册的显式类型明确报错，空字符串、大小写和空格均按原值匹配。 */
    @Test
    public void shouldRejectUnknownAndUnregisteredExplicitTypes() {
        assertThrows(DashboardDatasourceTypeNotSupportedException.class,
                () -> DashboardDatasourceHandlerFactory.getHandler("datawarehouse"));
        refresh("tenant", new StubHandler("datawarehouse"));
        for (String type : new String[]{"", " \t\r\n", "integration", "unknown", " datawarehouse ", "DATAWAREHOUSE"}) {
            assertThrows(DashboardDatasourceTypeNotSupportedException.class,
                    () -> DashboardDatasourceHandlerFactory.getHandler(type));
        }
        assertEquals(1, handlers.size());
    }

    /** 不同模块的刷新事件追加各自类型，不清空先前模块已注册的实现。 */
    @Test
    public void shouldAppendHandlersFromDifferentModules() {
        IDashboardDatasourceHandler datawarehouse = new StubHandler("datawarehouse");
        IDashboardDatasourceHandler integration = new StubHandler("integration");
        refresh("tenant", datawarehouse);
        refresh("integration", integration);
        assertSame(datawarehouse, DashboardDatasourceHandlerFactory.getHandler("datawarehouse"));
        assertSame(integration, DashboardDatasourceHandlerFactory.getHandler("integration"));
        assertEquals(2, handlers.size());
    }

    /** 同一 Bean 重复收到刷新事件或被再次扫描时不产生冲突。 */
    @Test
    public void shouldAllowRepeatedRegistrationOfSameBean() {
        IDashboardDatasourceHandler handler = new StubHandler("datawarehouse");
        NeatLogicWebApplicationContext context = context("tenant", handler);
        ContextRefreshedEvent event = new ContextRefreshedEvent(context);
        factory.onApplicationEvent(event);
        factory.onApplicationEvent(event);
        refresh("another-module", handler);
        assertSame(handler, DashboardDatasourceHandlerFactory.getHandler("datawarehouse"));
        assertEquals(1, handlers.size());
        // 重复事件不能在对外的类型选项中产生重复记录。
        assertEquals(1, DashboardDatasourceHandlerFactory.getTypeList().size());
        assertEquals("datawarehouse", DashboardDatasourceHandlerFactory.getTypeList().get(0).getString("value"));
    }

    /** 不同 Bean 声明同一类型使初始化失败，同时保留已注册实例。 */
    @Test
    public void shouldRejectDuplicateTypeWithoutReplacingHandler() {
        IDashboardDatasourceHandler handler = new StubHandler("datawarehouse");
        refresh("tenant", handler);
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> refresh("duplicate-module", new StubHandler("datawarehouse")));
        assertTrue(exception.getMessage().contains("datawarehouse"));
        assertTrue(exception.getMessage().contains("duplicate-module"));
        assertSame(handler, DashboardDatasourceHandlerFactory.getHandler("datawarehouse"));
        assertEquals(1, handlers.size());
    }

    /** 空白类型属于实现配置错误，在模块初始化阶段拒绝而不是使用默认类型注册。 */
    @Test
    public void shouldRejectBlankHandlerTypes() {
        for (String type : new String[]{null, "", " \t\r\n"}) {
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> refresh("invalid-module", new StubHandler(type)));
            assertTrue(exception.getMessage().contains("invalid-module"));
            assertTrue(handlers.isEmpty());
        }
    }

    /** root 初始化和非模块事件不进行注册，由真实模块事件统一装配。 */
    @Test
    public void shouldIgnoreNonModuleContextRefresh() {
        factory.init();
        factory.onApplicationEvent(new ContextRefreshedEvent(new GenericApplicationContext()));
        assertTrue(handlers.isEmpty());
    }

    /** 并发注册同一类型仅允许一个实例成功，另一个必须显式报错且不能覆盖。 */
    @Test
    public void shouldRejectConcurrentDuplicateRegistration() throws Exception {
        IDashboardDatasourceHandler first = new StubHandler("datawarehouse");
        IDashboardDatasourceHandler second = new StubHandler("datawarehouse");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Future<IllegalStateException> firstResult = executor.submit(() -> registerConcurrently("first", first, ready, start));
            Future<IllegalStateException> secondResult = executor.submit(() -> registerConcurrently("second", second, ready, start));
            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            IllegalStateException firstError = firstResult.get(5, TimeUnit.SECONDS);
            IllegalStateException secondError = secondResult.get(5, TimeUnit.SECONDS);
            assertTrue((firstError == null) != (secondError == null));
            assertSame(firstError == null ? first : second, DashboardDatasourceHandlerFactory.getHandler("datawarehouse"));
            assertEquals(1, handlers.size());
        } finally {
            start.countDown();
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    /** 尚未加载任何模块时返回可独立使用的空列表。 */
    @Test
    public void shouldReturnIndependentEmptyTypeLists() {
        List<JSONObject> first = DashboardDatasourceHandlerFactory.getTypeList();
        List<JSONObject> second = DashboardDatasourceHandlerFactory.getTypeList();
        assertTrue(first.isEmpty());
        assertTrue(second.isEmpty());
        assertNotSame(first, second);
    }

    /** 新增类型无需修改工厂；数据仓库优先，其余按标识升序，与注册顺序无关。 */
    @Test
    public void shouldListDefaultFirstAndSortAdditionalTypes() {
        refresh("z-module", new StubHandler("z-source"));
        refresh("integration-module", new StubHandler("integration"));
        refresh("a-module", new StubHandler("a-source"));
        refresh("warehouse-module", new StubHandler("datawarehouse"));
        List<JSONObject> options = DashboardDatasourceHandlerFactory.getTypeList();
        assertEquals(Arrays.asList("datawarehouse", "a-source", "integration", "z-source"),
                options.stream().map(option -> option.getString("value")).collect(Collectors.toList()));
        for (JSONObject option : options) {
            assertEquals(2, option.size());
            assertEquals(option.getString("value"), option.getString("text"));
        }
    }

    /** 未注册数据仓库时不补造默认选项，仅排序当前已注册的类型。 */
    @Test
    public void shouldSortOnlyRegisteredTypesWithoutDefault() {
        refresh("integration-module", new StubHandler("integration"));
        refresh("a-module", new StubHandler("a-source"));
        assertEquals(Arrays.asList("a-source", "integration"), DashboardDatasourceHandlerFactory.getTypeList()
                .stream().map(option -> option.getString("value")).collect(Collectors.toList()));
    }

    /** 调用方修改列表或其中的 JSON 对象，不影响后续响应和内部注册表。 */
    @Test
    public void shouldReturnIndependentListsAndOptionObjects() {
        IDashboardDatasourceHandler handler = new StubHandler("datawarehouse");
        refresh("framework", handler);
        List<JSONObject> first = DashboardDatasourceHandlerFactory.getTypeList();
        List<JSONObject> second = DashboardDatasourceHandlerFactory.getTypeList();
        assertNotSame(first, second);
        assertNotSame(first.get(0), second.get(0));
        first.get(0).put("value", "changed");
        first.get(0).put("text", "changed");
        first.clear();
        assertEquals("datawarehouse", second.get(0).getString("value"));
        assertEquals("datawarehouse", second.get(0).getString("text"));
        assertEquals(second, DashboardDatasourceHandlerFactory.getTypeList());
        assertSame(handler, DashboardDatasourceHandlerFactory.getHandler("datawarehouse"));
        assertEquals(1, handlers.size());
    }

    /** 每次列表调用都读取当前展示名称，模拟请求语言切换而不缓存旧翻译。 */
    @Test
    public void shouldResolveTypeNameOnEveryListCall() {
        AtomicReference<String> name = new AtomicReference<>("数据仓库");
        AtomicInteger nameCalls = new AtomicInteger();
        refresh("framework", new StubHandler("datawarehouse") {
            /** 每次调用返回当前模拟语言，并记录实际解析次数。 */
            @Override
            public String getTypeName() {
                nameCalls.incrementAndGet();
                return name.get();
            }
        });
        List<JSONObject> chinese = DashboardDatasourceHandlerFactory.getTypeList();
        name.set("Data Warehouse");
        List<JSONObject> english = DashboardDatasourceHandlerFactory.getTypeList();
        name.set("数据仓库");
        assertEquals("数据仓库", DashboardDatasourceHandlerFactory.getTypeList().get(0).getString("text"));
        assertEquals("数据仓库", chinese.get(0).getString("text"));
        assertEquals("Data Warehouse", english.get(0).getString("text"));
        assertEquals(3, nameCalls.get());
    }

    /** 名称解析期间新增注册项只影响下一次列表，当前响应使用调用开始时的快照。 */
    @Test
    public void shouldUseRegistrySnapshotWhenResolvingNames() {
        StubHandler integration = new StubHandler("integration");
        refresh("framework", new StubHandler("datawarehouse") {
            /** 在名称解析时触发另一个模块的真实刷新事件。 */
            @Override
            public String getTypeName() {
                refresh("integration-module", integration);
                return "数据仓库";
            }
        });
        assertEquals(1, DashboardDatasourceHandlerFactory.getTypeList().size());
        assertEquals(Arrays.asList("datawarehouse", "integration"), DashboardDatasourceHandlerFactory.getTypeList()
                .stream().map(option -> option.getString("value")).collect(Collectors.toList()));
    }

    /** 使用屏障让两个真实模块事件竞争同一个注册项，仅捕获预期的类型冲突。 */
    private IllegalStateException registerConcurrently(String moduleId, IDashboardDatasourceHandler handler,
                                                       CountDownLatch ready, CountDownLatch start) throws InterruptedException {
        ready.countDown();
        if (!start.await(5, TimeUnit.SECONDS)) {
            throw new AssertionError("并发注册未收到开始信号");
        }
        try {
            refresh(moduleId, handler);
            return null;
        } catch (IllegalStateException exception) {
            return exception;
        }
    }

    /** 走父类真实的事件判断和分发流程，而不是直接调用注册方法。 */
    private void refresh(String moduleId, IDashboardDatasourceHandler handler) {
        factory.onApplicationEvent(new ContextRefreshedEvent(context(moduleId, handler)));
    }

    /** 仅替代 Bean 扫描，返回传入的 Spring Bean 实例，不启动 XML 或数据库。 */
    private NeatLogicWebApplicationContext context(String moduleId, IDashboardDatasourceHandler handler) {
        NeatLogicWebApplicationContext context = new NeatLogicWebApplicationContext() {
            @Override
            public <T> Map<String, T> getBeansOfType(Class<T> type) {
                Map<String, T> beans = new LinkedHashMap<>();
                beans.put("dashboardDatasourceHandler", type.cast(handler));
                return beans;
            }
        };
        context.setId(moduleId);
        return context;
    }

    /** 测试 Handler 只声明类型，工厂注册与选择过程不能执行任何取数操作。 */
    private static class StubHandler implements IDashboardDatasourceHandler {
        private final String type;

        /** 为每个测试构造独立 Bean，区分重复实例与重复类型。 */
        private StubHandler(String type) {
            this.type = type;
        }

        /** 返回测试指定的原始类型。 */
        @Override
        public String getType() {
            return type;
        }

        /** 返回测试展示名称，不依赖实际语言资源。 */
        @Override
        public String getTypeName() {
            return type;
        }

        /** 注册和选择 Handler 不应触发列表查询。 */
        @Override
        public JSONObject listDatasource(JSONObject params) {
            throw new AssertionError("注册和选择 Handler 不应查询列表");
        }

        /** 详情契约使用 JSON，注册和选择 Handler 仍不应触发详情查询。 */
        @Override
        public JSONObject getDatasource(JSONObject params) {
            throw new AssertionError("注册和选择 Handler 不应查询详情");
        }

        /** 注册和选择 Handler 不应触发动态取数。 */
        @Override
        public JSONObject searchDatasourceData(JSONObject params) {
            throw new AssertionError("注册和选择 Handler 不应执行动态取数");
        }
    }
}
