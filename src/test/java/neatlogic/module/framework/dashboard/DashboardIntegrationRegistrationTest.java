package neatlogic.module.framework.dashboard;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.dashboard.datasource.core.DashboardDatasourceHandlerFactory;
import neatlogic.framework.dashboard.datasource.core.IDashboardDatasourceHandler;
import neatlogic.framework.integration.core.IIntegrationHandler;
import neatlogic.framework.integration.core.IntegrationHandlerFactory;
import neatlogic.framework.integration.dto.IntegrationHandlerVo;
import neatlogic.module.framework.dashboard.datasource.handler.DatawarehouseDashboardDatasourceHandler;
import neatlogic.module.framework.dashboard.datasource.handler.IntegrationDashboardDatasourceHandler;
import neatlogic.module.framework.dashboard.integration.handler.DashboardIntegrationHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/** 验证 framework 自身可扫描并注册大屏集成，无需加载租户或商业模块、数据库和 HTTP。 */
public class DashboardIntegrationRegistrationTest {
    private Map<String, IDashboardDatasourceHandler> datasourceHandlers;
    private Map<String, IDashboardDatasourceHandler> originalDatasourceHandlers;
    private Map<String, IIntegrationHandler> integrationHandlers;
    private Map<String, IIntegrationHandler> originalIntegrationHandlers;
    private List<IIntegrationHandler> integrationHandlerList;
    private List<IIntegrationHandler> originalIntegrationHandlerList;

    /** 保留两个工厂的原注册内容，只清空集合内容，不替换全局对象。 */
    @Before
    @SuppressWarnings("unchecked")
    public void initialize() throws Exception {
        datasourceHandlers = (Map<String, IDashboardDatasourceHandler>) staticField(
                DashboardDatasourceHandlerFactory.class, "handlerMap");
        integrationHandlers = (Map<String, IIntegrationHandler>) staticField(
                IntegrationHandlerFactory.class, "componentMap");
        integrationHandlerList = (List<IIntegrationHandler>) staticField(
                IntegrationHandlerFactory.class, "handlerList");
        originalDatasourceHandlers = new HashMap<>(datasourceHandlers);
        originalIntegrationHandlers = new HashMap<>(integrationHandlers);
        originalIntegrationHandlerList = new ArrayList<>(integrationHandlerList);
        datasourceHandlers.clear();
        integrationHandlers.clear();
        integrationHandlerList.clear();
    }

    /** 按测试前的精确内容恢复全部静态 Map/List，避免顺序依赖或重复规范污染后续用例。 */
    @After
    public void restore() {
        if (datasourceHandlers != null && originalDatasourceHandlers != null) {
            datasourceHandlers.clear();
            datasourceHandlers.putAll(originalDatasourceHandlers);
        }
        if (integrationHandlers != null && originalIntegrationHandlers != null) {
            integrationHandlers.clear();
            integrationHandlers.putAll(originalIntegrationHandlers);
        }
        if (integrationHandlerList != null && originalIntegrationHandlerList != null) {
            integrationHandlerList.clear();
            integrationHandlerList.addAll(originalIntegrationHandlerList);
        }
    }

    /** 真实 Spring 候选扫描识别迁入的两个组件和原数据仓库实现，而非手工伪造候选集合。 */
    @Test
    public void discoversDashboardComponentsInFrameworkPackage() {
        Set<String> candidates = scanComponents().stream().map(BeanDefinition::getBeanClassName).collect(Collectors.toSet());
        assertTrue(candidates.contains(IntegrationDashboardDatasourceHandler.class.getName()));
        assertTrue(candidates.contains(DashboardIntegrationHandler.class.getName()));
        assertTrue(candidates.contains(DatawarehouseDashboardDatasourceHandler.class.getName()));
        assertEquals("neatlogic.module.framework.dashboard.datasource.handler",
                IntegrationDashboardDatasourceHandler.class.getPackage().getName());
        assertEquals("neatlogic.module.framework.dashboard.integration.handler",
                DashboardIntegrationHandler.class.getPackage().getName());
    }

    /** 用真实模块 refresh 触发两工厂监听器，数据仓库与集成同时可用且大屏集成规范只出现一次。 */
    @Test
    public void registersBothDatasourceTypesAndOneIntegrationPatternOnFrameworkRefresh() {
        Set<BeanDefinition> candidates = scanComponents();
        NeatLogicWebApplicationContext context = new NeatLogicWebApplicationContext() {
            @Override
            protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
                // 直接注册真实扫描结果，省略生产 XML 与数据库依赖装配；此用例只验证组件发现和注册。
                for (BeanDefinition candidate : candidates) {
                    beanFactory.registerBeanDefinition(candidate.getBeanClassName(), candidate);
                }
            }
        };
        context.setId("framework");
        context.setModuleId("framework");
        context.setGroup("framework");
        context.addApplicationListener(new DashboardDatasourceHandlerFactory());
        context.addApplicationListener(new IntegrationHandlerFactory());
        try {
            context.refresh();
            assertEquals(2, context.getBeansOfType(IDashboardDatasourceHandler.class).size());
            assertEquals(new HashSet<>(Arrays.asList("datawarehouse", "integration")), datasourceHandlers.keySet());
            assertSame(context.getBean(IntegrationDashboardDatasourceHandler.class),
                    DashboardDatasourceHandlerFactory.getHandler("integration"));
            assertSame(context.getBean(DatawarehouseDashboardDatasourceHandler.class),
                    DashboardDatasourceHandlerFactory.getHandler("datawarehouse"));
            // 使用真实已注册组件和 framework 语言资源验证类型选项随请求语言刷新。
            assertLocalizedDatasourceTypes();

            DashboardIntegrationHandler pattern = context.getBean(DashboardIntegrationHandler.class);
            assertEquals(1, context.getBeansOfType(IIntegrationHandler.class).size());
            assertEquals("DashboardIntegrationHandler", pattern.getHandler());
            assertSame(pattern, IntegrationHandlerFactory.getHandler("DashboardIntegrationHandler"));
            assertEquals(1, integrationHandlers.size());
            assertEquals(1, integrationHandlerList.size());
            assertSame(pattern, integrationHandlerList.get(0));
            List<IntegrationHandlerVo> options = IntegrationHandlerFactory.getHandlerList();
            assertEquals(1, options.size());
            assertEquals("DashboardIntegrationHandler", options.get(0).getHandler());
        } finally {
            context.close();
        }
    }

    /** 连续切换真实请求语言，类型名称及列表均重新翻译，完成后恢复原线程上下文。 */
    private void assertLocalizedDatasourceTypes() {
        RequestContext originalContext = RequestContext.get();
        Locale originalLocale = originalContext == null ? null : originalContext.getLocale();
        RequestContext requestContext = originalContext == null ? RequestContext.init((RequestContext) null) : originalContext;
        try {
            for (Locale locale : Arrays.asList(Locale.CHINESE, Locale.ENGLISH, Locale.CHINESE)) {
                requestContext.setLocale(locale);
                boolean english = Locale.ENGLISH.equals(locale);
                String warehouseName = english ? "Data Warehouse" : "数据仓库";
                String integrationName = english ? "Integration" : "集成";
                assertEquals(warehouseName, DashboardDatasourceHandlerFactory.getHandler("datawarehouse").getTypeName());
                assertEquals(integrationName, DashboardDatasourceHandlerFactory.getHandler("integration").getTypeName());
                List<JSONObject> options = DashboardDatasourceHandlerFactory.getTypeList();
                assertEquals(2, options.size());
                assertEquals("datawarehouse", options.get(0).getString("value"));
                assertEquals(warehouseName, options.get(0).getString("text"));
                assertEquals("integration", options.get(1).getString("value"));
                assertEquals(integrationName, options.get(1).getString("text"));
            }
        } finally {
            if (originalContext == null) {
                requestContext.release();
            } else {
                originalContext.setLocale(originalLocale);
            }
        }
    }

    /** 默认注解过滤器覆盖 Component/Service 等元注解，与模块常规组件扫描规则一致。 */
    private Set<BeanDefinition> scanComponents() {
        return new ClassPathScanningCandidateComponentProvider(true)
                .findCandidateComponents("neatlogic.module.framework.dashboard");
    }

    /** 只为隔离测试读取已有静态注册表，不增加任何生产访问接口。 */
    private Object staticField(Class<?> owner, String name) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(null);
    }
}
