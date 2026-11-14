package neatlogic.module.framework.dashboard.datasource.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.core.AuthActionChecker;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.type.PermissionDeniedException;
import neatlogic.framework.integration.core.IIntegrationHandler;
import neatlogic.framework.integration.core.IRequestFrom;
import neatlogic.framework.integration.core.IntegrationDefaultValueUtil;
import neatlogic.framework.integration.core.IntegrationHandlerFactory;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.module.framework.integration.handler.FrameworkRequestFrom;
import neatlogic.module.framework.dashboard.integration.handler.DashboardIntegrationHandler;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/** 以真实大屏适配器、集成规范和权限规则验证行为，Mapper 与发送端均为内存替身。 */
public class IntegrationDashboardDatasourceHandlerTest {
    private static final String UUID = "12345678-1234-5678-abcd-123456789012";
    private static final String HANDLER = DashboardIntegrationHandler.class.getSimpleName();
    private IntegrationDashboardDatasourceHandler handler;
    private IntegrationVo integration;
    private IntegrationVo listQuery;
    private IntegrationVo countQuery;
    private List<IntegrationVo> listRows;
    private List<String> selectedUuids;
    private int countCalls;
    private int authorityCalls;
    private int authority = 1;
    private String detailUuid;
    private RecordingIntegrationHandler outbound;
    private Map<String, IIntegrationHandler> registry;
    private IIntegrationHandler originalHandler;
    private Field userMapperField;
    private Object originalUserMapper;
    private ThreadLocal<UserContext> userContextHolder;
    private UserContext originalUserContext;

    /** 隔离单个工厂键与当前线程用户，拒绝所有未声明的数据库访问。 */
    @Before
    @SuppressWarnings("unchecked")
    public void initialize() throws Exception {
        handler = new IntegrationDashboardDatasourceHandler();
        integration = new IntegrationVo();
        integration.setUuid(UUID);
        integration.setName("子作业集成");
        integration.setHandler(HANDLER);
        integration.setIsActive(1);
        integration.setUrl("https://must-not-call.invalid/private-token");
        integration.setMethod("post");
        integration.setConfig(config(headers()).toJSONString());
        listRows = Collections.singletonList(integration);
        IntegrationMapper mapper = (IntegrationMapper) Proxy.newProxyInstance(IntegrationMapper.class.getClassLoader(),
                new Class<?>[]{IntegrationMapper.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "searchIntegration":
                            listQuery = (IntegrationVo) args[0];
                            return listRows;
                        case "getIntegrationListByUuidList":
                            selectedUuids = (List<String>) args[0];
                            return listRows;
                        case "searchIntegrationCount":
                            countCalls++;
                            countQuery = (IntegrationVo) args[0];
                            return 41;
                        case "getIntegrationByUuid":
                            detailUuid = (String) args[0];
                            return integration;
                        case "checkUserHasIntegrationAuthority":
                            authorityCalls++;
                            assertEquals(UUID, args[0]);
                            assertEquals("execute", args[1]);
                            assertEquals("dashboard-test-user", args[2]);
                            assertEquals(Collections.singletonList("team-a"), args[3]);
                            assertEquals(Collections.singletonList("role-a"), args[4]);
                            return authority;
                        default:
                            throw new AssertionError("禁止访问未声明的 Mapper：" + method.getName());
                    }
                });
        Field mapperField = IntegrationDashboardDatasourceHandler.class.getDeclaredField("integrationMapper");
        mapperField.setAccessible(true);
        mapperField.set(handler, mapper);
        Field registryField = IntegrationHandlerFactory.class.getDeclaredField("componentMap");
        registryField.setAccessible(true);
        registry = (Map<String, IIntegrationHandler>) registryField.get(null);
        outbound = new RecordingIntegrationHandler();
        originalHandler = registry.put(HANDLER, outbound);

        userMapperField = AuthActionChecker.class.getDeclaredField("userMapper");
        userMapperField.setAccessible(true);
        originalUserMapper = userMapperField.get(null);
        UserMapper userMapper = (UserMapper) Proxy.newProxyInstance(UserMapper.class.getClassLoader(),
                new Class<?>[]{UserMapper.class}, (proxy, method, args) -> {
                    if ("searchUserAllAuthByUserAuth".equals(method.getName())) return Collections.emptyList();
                    throw new AssertionError("禁止访问用户数据库：" + method.getName());
                });
        new AuthActionChecker().setUserMapper(userMapper);
        Field contextField = UserContext.class.getDeclaredField("instance");
        contextField.setAccessible(true);
        userContextHolder = (ThreadLocal<UserContext>) contextField.get(null);
        originalUserContext = userContextHolder.get();
        Constructor<UserContext> constructor = UserContext.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        UserContext user = constructor.newInstance();
        user.setUserUuid("dashboard-test-user");
        user.setIsSuperAdmin(false);
        AuthenticationInfoVo auth = new AuthenticationInfoVo("dashboard-test-user");
        auth.getTeamUuidList().add("team-a");
        auth.getRoleUuidList().add("role-a");
        user.setAuthenticationInfoVo(auth);
        userContextHolder.set(user);
    }

    /** 恢复精确的静态依赖与原线程上下文，不影响同 JVM 的其他模块测试。 */
    @After
    public void restore() throws Exception {
        if (registry != null) {
            if (originalHandler == null) registry.remove(HANDLER); else registry.put(HANDLER, originalHandler);
        }
        if (userMapperField != null) userMapperField.set(null, originalUserMapper);
        if (userContextHolder != null) {
            if (originalUserContext == null) userContextHolder.remove(); else userContextHolder.set(originalUserContext);
        }
    }

    /** 普通列表强制限定启用状态和大屏规范，只暴露 ID 与名称并保留分页。 */
    @Test
    public void listsOnlyEnabledDashboardIntegrationsUsingSafeProjection() {
        JSONObject result = handler.listDatasource(JSON.parseObject("{\"keyword\":\"作业\",\"currentPage\":2,\"pageSize\":10,\"handler\":\"OtherHandler\",\"isActive\":0,\"url\":\"ignored\"}"));
        assertEquals("integration", handler.getType());
        assertEquals("作业", listQuery.getKeyword());
        assertEquals(Integer.valueOf(1), listQuery.getIsActive());
        assertEquals(HANDLER, listQuery.getHandler());
        assertEquals(Boolean.TRUE, listQuery.getNeedPage());
        assertEquals(Integer.valueOf(10), listQuery.getStartNum());
        assertEquals(HANDLER, countQuery.getHandler());
        assertEquals(Integer.valueOf(1), countQuery.getIsActive());
        assertEquals(2, result.getIntValue("currentPage"));
        assertEquals(10, result.getIntValue("pageSize"));
        assertEquals(41, result.getIntValue("rowNum"));
        assertEquals(5, result.getIntValue("pageCount"));
        JSONObject item = result.getJSONArray("tbodyList").getJSONObject(0);
        assertEquals(set("id", "label"), item.keySet());
        assertEquals(UUID, item.getString("id"));
        assertEquals("子作业集成", item.getString("label"));
        assertFalse(result.toJSONString().contains("private-token"));
        assertEquals(0, outbound.calls);
    }

    /** defaultValue 使用独立 UUID 列表查询，不执行普通分页查询和计数。 */
    @Test
    public void selectedUuidsOverrideIdListWithoutPaginationOrCountQuery() {
        JSONArray ids = new JSONArray();
        listRows = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            ids.add(UUID + "-" + i);
            IntegrationVo row = new IntegrationVo();
            row.setUuid(UUID + "-" + i);
            row.setName("来源" + i);
            listRows.add(row);
        }
        JSONObject params = new JSONObject().fluentPut("defaultValue", ids).fluentPut("idList", Collections.singletonList("wrong"))
                .fluentPut("currentPage", 3).fluentPut("pageSize", 1).fluentPut("keyword", "ignored");
        JSONObject result = handler.listDatasource(params);
        assertEquals(ids.toJavaList(String.class), selectedUuids);
        assertNull(listQuery);
        assertEquals(25, result.getJSONArray("tbodyList").size());
        assertEquals(25, result.getIntValue("rowNum"));
        assertEquals(0, countCalls);
    }

    /** 空 defaultValue 走普通分页查询，idList 不触发 UUID 回显，空结果仍保留分页容器。 */
    @Test
    public void emptyDefaultValueUsesRegularSearchAndPreservesEmptyResults() {
        handler.listDatasource(new JSONObject().fluentPut("defaultValue", new JSONArray()).fluentPut("idList", Collections.singletonList(UUID)));
        assertTrue(listQuery.getDefaultValue().isEmpty());
        assertEquals(Boolean.TRUE, listQuery.getNeedPage());
        assertNull(selectedUuids);
        assertEquals(1, countCalls);
        listRows = Collections.emptyList();
        JSONObject result = handler.listDatasource(new JSONObject());
        assertTrue(result.getJSONArray("tbodyList").isEmpty());
        assertTrue(result.containsKey("currentPage"));
        assertTrue(result.containsKey("pageSize"));
        assertTrue(result.containsKey("rowNum"));
        assertTrue(result.containsKey("pageCount"));
    }

    /** 使用真实 Mapper XML，分别验证普通查询分页和 UUID 列表查询的参数绑定。 */
    @Test
    public void selectedUuidQueryOmitsLimitInExistingMybatisSql() throws Exception {
        Configuration configuration = new Configuration();
        // 注册项目原有类型别名，使完整 Mapper XML 在隔离配置中按真实规则解析。
        configuration.getTypeAliasRegistry().registerAlias("CompressHandler", neatlogic.framework.dao.plugin.CompressHandler.class);
        String resource = "neatlogic/framework/integration/dao/mapper/IntegrationMapper.xml";
        try (InputStream input = IntegrationMapper.class.getClassLoader().getResourceAsStream(resource)) {
            assertNotNull("复用的 Mapper XML 应进入模块资源", input);
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
        String statement = IntegrationMapper.class.getName() + ".searchIntegration";
        handler.listDatasource(new JSONObject().fluentPut("keyword", "job").fluentPut("currentPage", 2));
        String normalSql = configuration.getMappedStatement(statement).getBoundSql(listQuery).getSql();
        assertTrue(normalSql.contains("LIMIT"));
        handler.listDatasource(new JSONObject().fluentPut("defaultValue", Collections.singletonList(UUID)));
        assertTrue(normalSql.contains("handler = ?"));
        assertTrue(normalSql.contains("is_active = ?"));
        String echoStatement = IntegrationMapper.class.getName() + ".getIntegrationListByUuidList";
        String echoSql = configuration.getMappedStatement(echoStatement)
                .getBoundSql(Collections.singletonMap("list", selectedUuids)).getSql();
        assertFalse(echoSql.contains("LIMIT"));
        assertTrue(echoSql.contains("`uuid` IN"));
        assertEquals(Collections.singletonList(UUID), selectedUuids);
        assertFalse(echoSql.contains(UUID));
    }

    /** 详情仅返回六属性字段，默认类型和原始控件配置来自配置表头，完全不执行集成。 */
    @Test
    public void detailUsesMinimalConfiguredFieldsAndPreservesSourceConfig() {
        JSONObject original = JSON.parseObject(integration.getConfig().toJSONString());
        JSONObject detail = handler.getDatasource(request());
        assertEquals(UUID, detailUuid);
        assertEquals(Collections.singleton("fieldList"), detail.keySet());
        JSONArray fields = detail.getJSONArray("fieldList");
        assertEquals(2, fields.size());
        for (Object item : fields) assertEquals(set("id", "label", "type", "isCondition", "inputType", "config"), ((JSONObject) item).keySet());
        JSONObject first = fields.getJSONObject(0);
        assertEquals("name", first.getString("id"));
        assertEquals("作业名", first.getString("label"));
        assertEquals("text", first.getString("type"));
        assertEquals("text", first.getString("inputType"));
        assertEquals(0, first.getIntValue("isCondition"));
        assertTrue(first.containsKey("config"));
        assertNull(first.get("config"));
        JSONObject second = fields.getJSONObject(1);
        assertEquals("number", second.getString("type"));
        assertEquals("select", second.getString("inputType"));
        assertEquals(1, second.getIntValue("isCondition"));
        assertEquals(headers().getJSONObject(1).getJSONObject("config"), second.getJSONObject("config"));
        assertEquals(original, integration.getConfig());
        assertEquals(0, outbound.calls);
        assertEquals(0, authorityCalls);
    }

    /** 输出默认表头关闭、缺失、为空或非数组时，详情与取数均失败且不执行集成推断字段。 */
    @Test
    public void rejectsMissingDisabledAndEmptyHeaderDefaults() {
        List<JSONObject> configs = new ArrayList<>();
        configs.add(new JSONObject());
        JSONObject disabled = config(headers());
        defaults(disabled).put("enabled", false);
        configs.add(disabled);
        JSONObject missing = config(headers());
        defaults(missing).remove("value");
        configs.add(missing);
        configs.add(config(new JSONArray()));
        for (Object invalid : Arrays.asList(new JSONObject(), "not-an-array", 1)) {
            JSONObject malformed = config(headers());
            defaults(malformed).put("value", invalid);
            configs.add(malformed);
        }
        for (JSONObject value : configs) {
            integration.setConfig(value.toJSONString());
            assertThrows(ApiRuntimeException.class, () -> handler.getDatasource(request()));
            assertThrows(ApiRuntimeException.class, () -> handler.searchDatasourceData(request()));
        }
        assertEquals(0, outbound.calls);
    }

    /** 表头 key 唯一且不可空白，title 必须存在；数字开关 1 与布尔 true 均有效。 */
    @Test
    public void rejectsDuplicateBlankKeysAndMissingTitlesButAcceptsNumericEnabled() {
        for (JSONArray invalid : Arrays.asList(JSON.parseArray("[{\"key\":\"a\",\"title\":\"A\"},{\"key\":\"a\",\"title\":\"B\"}]"),
                JSON.parseArray("[{\"key\":\" \",\"title\":\"A\"}]"), JSON.parseArray("[{\"key\":\"a\"}]"),
                JSON.parseArray("[{\"title\":\"A\"}]"), JSON.parseArray("[{\"key\":\"a\",\"title\":\" \"}]"))) {
            integration.setConfig(config(invalid).toJSONString());
            assertThrows(ApiRuntimeException.class, () -> handler.getDatasource(request()));
            assertThrows(ApiRuntimeException.class, () -> handler.searchDatasourceData(request()));
        }
        JSONObject numeric = config(headers());
        defaults(numeric).put("enabled", 1);
        integration.setConfig(numeric.toJSONString());
        assertEquals(2, handler.getDatasource(request()).getJSONArray("fieldList").size());
        assertEquals(0, outbound.calls);
    }

    /** 仅有旧输入默认表头不能作为详情或取数的字段来源，拒绝前不会进入集成发送链。 */
    @Test
    public void rejectsInputOnlyHeadersWithoutExecutingIntegration() {
        JSONObject legacy = config(headers());
        legacy.getJSONObject("param").getJSONArray("builtinDefaultList").getJSONObject(0).put("mode", "input");
        integration.setConfig(legacy.toJSONString());
        assertThrows(ApiRuntimeException.class, () -> handler.getDatasource(request()));
        assertThrows(ApiRuntimeException.class, () -> handler.searchDatasourceData(request()));
        assertEquals(0, outbound.calls);
        assertEquals(0, outbound.sends);
        assertEquals(0, outbound.validations);
    }

    /** 新输出与已启用的旧输入并存时，公共保存及执行规则继续拒绝，真实输入准备失败后不会发送。 */
    @Test
    public void rejectsEnabledLegacyInputAlongsideOutputBeforeSending() {
        JSONObject config = config(headers());
        appendLegacyInput(config, true);
        integration.setConfig(config.toJSONString());
        assertThrows(ApiRuntimeException.class, () -> IntegrationDefaultValueUtil.validate(outbound, config));
        assertThrows(ApiRuntimeException.class, () -> handler.searchDatasourceData(request()));
        assertEquals(1, outbound.calls);
        assertEquals(0, outbound.sends);
        assertEquals(0, outbound.validations);
        assertEquals(config, integration.getConfig());
    }

    /** 禁用的旧输入草稿可保留，但不同方向的同名表头只读取已启用的输出配置。 */
    @Test
    public void ignoresDisabledLegacyInputHeaderAndUsesOutputDefaults() throws PermissionDeniedException {
        JSONObject config = config(headers());
        appendLegacyInput(config, false);
        integration.setConfig(config.toJSONString());
        IntegrationDefaultValueUtil.validate(outbound, config);
        JSONArray fields = handler.getDatasource(request()).getJSONArray("fieldList");
        assertEquals("name", fields.getJSONObject(0).getString("id"));
        assertEquals("duration", fields.getJSONObject(1).getString("id"));
        JSONObject result = handler.searchDatasourceData(request());
        assertEquals("name", result.getJSONArray("theadList").getJSONObject(0).getString("key"));
        assertEquals(headers(), JSON.parseObject(outbound.response.getTransformedResult()).getJSONArray("theadList"));
        assertFalse(outbound.parameters.containsKey("theadList"));
        assertEquals(config, integration.getConfig());
        assertEquals(1, outbound.sends);
    }

    /** 取数保留远端显式表头及原始行字段，详情仍读取配置中的输出默认表头。 */
    @Test
    public void preservesRemoteHeadersAndRowsWhileDetailUsesConfiguredFields() throws PermissionDeniedException {
        JSONObject remote = JSON.parseObject(outbound.response.getTransformedResult());
        JSONArray remoteHeaders = JSON.parseArray("[{\"key\":\"remote\",\"title\":\"远端字段\"}]");
        remote.put("theadList", remoteHeaders);
        remote.put("tbodyList", JSON.parseArray("[{\"name\":\"作业A\",\"duration\":6,\"remote\":\"不得映射\"}]"));
        outbound.response.setTransformedResult(remote.toJSONString());
        JSONObject result = handler.searchDatasourceData(request());
        assertEquals(remoteHeaders, JSON.parseObject(outbound.response.getTransformedResult()).getJSONArray("theadList"));
        assertEquals("name", handler.getDatasource(request()).getJSONArray("fieldList").getJSONObject(0).getString("id"));
        assertEquals(remoteHeaders, result.getJSONArray("theadList"));
        assertEquals(remote.getJSONArray("tbodyList"), result.getJSONArray("tbodyList"));
        assertFalse(outbound.parameters.containsKey("theadList"));
    }

    /** 来源不存在、停用或规范不符时阻止执行，详情不存在继续返回 null。 */
    @Test
    public void refusesMissingDisabledAndWrongHandlerSources() {
        IntegrationVo saved = integration;
        integration = null;
        assertNull(handler.getDatasource(request()));
        assertThrows(ApiRuntimeException.class, () -> handler.searchDatasourceData(request()));
        integration = saved;
        integration.setIsActive(0);
        assertThrows(ApiRuntimeException.class, () -> handler.searchDatasourceData(request()));
        integration.setIsActive(1);
        integration.setHandler("DataWareHouseIntegrationHandler");
        assertThrows(ApiRuntimeException.class, () -> handler.searchDatasourceData(request()));
        assertEquals(0, outbound.calls);
    }

    /** 执行输入只接受白名单，取数保留远端原始行字段，缺失表头由配置默认值补齐。 */
    @Test
    public void forwardsOnlyQueryParametersAndPreservesRemoteRows() throws PermissionDeniedException {
        JSONObject params = request().fluentPut("conditionList", JSON.parseArray("[{\"id\":\"duration\",\"value\":5}]"))
                .fluentPut("sortList", JSON.parseArray("[{\"fieldId\":\"duration\",\"type\":\"DESC\"}]"))
                .fluentPut("currentPage", 2).fluentPut("pageSize", 5).fluentPut("isExpired", 0)
                .fluentPut("theadList", JSON.parseArray("[{\"key\":\"injected\",\"title\":\"Injected\"}]"))
                .fluentPut("userUuid", "injected").fluentPut("handler", "injected").fluentPut("url", "injected")
                .fluentPut("keyword", "injected").fluentPut("needPage", false).fluentPut("custom", "injected");
        JSONObject originalParams = JSON.parseObject(params.toJSONString());
        JSONObject originalConfig = JSON.parseObject(integration.getConfig().toJSONString());
        outbound.response.setTransformedResult("{\"tbodyList\":[{\"name\":\"作业A\",\"duration\":0,\"private\":\"hidden\"},{\"name\":\"作业B\"}],\"currentPage\":7,\"pageSize\":3,\"rowNum\":12,\"pageCount\":4}");
        JSONObject result = handler.searchDatasourceData(params);
        assertEquals(set("conditionList", "sortList", "currentPage", "pageSize", "isExpired", "needPage"), outbound.parameters.keySet());
        assertEquals(params.getJSONArray("conditionList"), outbound.parameters.getJSONArray("conditionList"));
        assertEquals(params.getJSONArray("sortList"), outbound.parameters.getJSONArray("sortList"));
        assertEquals(2, outbound.parameters.getIntValue("currentPage"));
        assertEquals(5, outbound.parameters.getIntValue("pageSize"));
        assertTrue(outbound.parameters.getBooleanValue("needPage"));
        assertFalse(outbound.parameters.containsKey("theadList"));
        // 远端未给出表头时，发送基类公共输出补齐链路提供配置默认值，且不反向写入输入。
        assertEquals(headers(), JSON.parseObject(outbound.response.getTransformedResult()).getJSONArray("theadList"));
        assertEquals(headers(), result.getJSONArray("theadList"));
        JSONArray rows = result.getJSONArray("tbodyList");
        assertEquals(JSON.parseObject(outbound.response.getTransformedResult()).getJSONArray("tbodyList"), rows);
        assertEquals(set("name", "duration", "private"), rows.getJSONObject(0).keySet());
        assertEquals(0, rows.getJSONObject(0).getIntValue("duration"));
        assertEquals("作业B", rows.getJSONObject(1).getString("name"));
        assertFalse(rows.getJSONObject(1).containsKey("duration"));
        assertEquals(7, result.getIntValue("currentPage"));
        assertEquals(3, result.getIntValue("pageSize"));
        assertEquals(12, result.getIntValue("rowNum"));
        assertEquals(4, result.getIntValue("pageCount"));
        assertEquals(originalParams, params);
        assertEquals(originalConfig, integration.getConfig());
        assertEquals(1, authorityCalls);
        assertEquals(1, outbound.validations);
        assertSame(FrameworkRequestFrom.DASHBOARD, outbound.requestFrom);
        assertEquals(Long.valueOf(123L), outbound.response.getAuditId());
    }

    /** limit 缺省分页、0 不限行和正数限行三态按原值送达集成，结果分页不本地改写。 */
    @Test
    public void preservesThreeLimitModesAndDoesNotTruncateLocally() throws PermissionDeniedException {
        JSONObject params = request();
        handler.searchDatasourceData(params);
        assertFalse(outbound.parameters.containsKey("limit"));
        assertTrue(outbound.parameters.getBooleanValue("needPage"));
        assertEquals(1, outbound.parameters.getIntValue("currentPage"));
        assertEquals(20, outbound.parameters.getIntValue("pageSize"));
        for (int limit : new int[]{0, 1}) {
            params.put("limit", limit);
            outbound.response.setTransformedResult("{\"tbodyList\":[{},{}],\"currentPage\":9,\"pageSize\":11,\"rowNum\":99,\"pageCount\":9}");
            JSONObject result = handler.searchDatasourceData(params);
            assertEquals(limit, outbound.parameters.getIntValue("limit"));
            assertFalse(outbound.parameters.getBooleanValue("needPage"));
            assertEquals(2, result.getJSONArray("tbodyList").size());
            assertEquals(9, result.getIntValue("currentPage"));
            assertEquals(99, result.getIntValue("rowNum"));
        }
        outbound.response.setTransformedResult("{\"tbodyList\":[],\"currentPage\":1,\"pageSize\":20,\"rowNum\":0,\"pageCount\":0}");
        JSONObject empty = handler.searchDatasourceData(params);
        assertTrue(empty.getJSONArray("tbodyList").isEmpty());
        assertEquals(2, empty.getJSONArray("theadList").size());
    }

    /** 集成自身的自定义默认值继续补齐，客户端同名额外参数不越过大屏白名单。 */
    @Test
    public void preservesConfiguredCustomDefaultsWithoutAcceptingClientOverride() throws PermissionDeniedException {
        JSONObject config = config(headers());
        config.getJSONObject("param").put("paramList", JSON.parseArray("[{\"name\":\"tenantCode\",\"mode\":\"input\",\"type\":\"string\",\"isRequired\":0,\"defaultConfig\":{\"enabled\":true,\"value\":\"configured-bank\"}}]"));
        integration.setConfig(config.toJSONString());
        JSONObject before = JSON.parseObject(integration.getConfig().toJSONString());
        handler.searchDatasourceData(request().fluentPut("tenantCode", "client-override"));
        assertEquals("configured-bank", outbound.parameters.getString("tenantCode"));
        assertEquals(before, integration.getConfig());
        assertEquals(1, outbound.calls);
    }

    /** 执行授权按当前用户、组与角色检查，失败不会调用集成或进行输出转换。 */
    @Test
    public void refusesExecutionWithoutAuthorityAndAllowsExplicitGrant() throws PermissionDeniedException {
        authority = 0;
        assertThrows(PermissionDeniedException.class, () -> handler.searchDatasourceData(request()));
        assertEquals(1, authorityCalls);
        assertEquals(0, outbound.calls);
        assertEquals(0, outbound.validations);
        authority = 1;
        handler.searchDatasourceData(request());
        assertEquals(2, authorityCalls);
        assertEquals(1, outbound.calls);
        assertSame(FrameworkRequestFrom.DASHBOARD, outbound.requestFrom);
        userContextHolder.remove();
        assertThrows(PermissionDeniedException.class, () -> handler.searchDatasourceData(request()));
        assertEquals(1, outbound.calls);
    }

    /** 已记录调用错误不能被默认表头或输出校验掩盖，原审计对象及错误内容保持不变。 */
    @Test
    public void rejectsRequestAndTransformationErrorsBeforeRowMapping() {
        outbound.response.appendError("转换失败：invalid-output");
        assertThrows(ApiRuntimeException.class, () -> handler.searchDatasourceData(request()));
        assertEquals(1, outbound.calls);
        assertEquals(0, outbound.validations);
        assertEquals("转换失败：invalid-output", outbound.response.getError());
        assertEquals(Long.valueOf(123L), outbound.response.getAuditId());
        assertSame(FrameworkRequestFrom.DASHBOARD, outbound.requestFrom);
        outbound.response = successfulResponse();
        outbound.failure = new IllegalStateException("传输失败");
        assertThrows(RuntimeException.class, () -> handler.searchDatasourceData(request()));
        assertEquals(2, outbound.calls);
        assertEquals(0, outbound.validations);
    }

    /** 没有注册规范处理器或返回形状错误时明确失败，不以空成功替代。 */
    @Test
    public void rejectsMissingHandlerAndMalformedTransformedResult() {
        registry.remove(HANDLER);
        assertThrows(ApiRuntimeException.class, () -> handler.searchDatasourceData(request()));
        assertEquals(0, outbound.calls);
        registry.put(HANDLER, outbound);
        outbound.response.setTransformedResult("{\"tbodyList\":[\"bad-row\"],\"currentPage\":1,\"pageSize\":20,\"rowNum\":1,\"pageCount\":1}");
        assertThrows(ApiRuntimeException.class, () -> handler.searchDatasourceData(request()));
        assertEquals(1, outbound.validations);
    }

    /** 构造带连字符 UUID 的详情与取数请求，确保从未按 Long 解析。 */
    private static JSONObject request() {
        return new JSONObject().fluentPut("id", UUID).fluentPut("dataSourceId", UUID).fluentPut("datasourceType", "integration");
    }

    /** 字段缺省值与完整控件配置共存，便于检查最小详情和原样传递。 */
    private static JSONArray headers() {
        return JSON.parseArray("[{\"key\":\"name\",\"title\":\"作业名\"},{\"key\":\"duration\",\"title\":\"耗时\",\"type\":\"number\",\"isCondition\":1,\"inputType\":\"select\",\"config\":{\"dataList\":[{\"value\":1,\"text\":\"一\"}],\"isMultiple\":false,\"params\":{\"nested\":true}}}]");
    }

    /** 固定输出默认值存储位置与集成编辑页一致，不读取输入方向同名表头。 */
    private static JSONObject config(JSONArray headerList) {
        JSONObject entry = new JSONObject().fluentPut("handler", HANDLER).fluentPut("mode", "output").fluentPut("name", "theadList")
                .fluentPut("defaultConfig", new JSONObject().fluentPut("enabled", true).fluentPut("value", headerList));
        return new JSONObject().fluentPut("param", new JSONObject().fluentPut("builtinDefaultList", new JSONArray(Collections.singletonList(entry))));
    }

    /** 用不同字段构造旧输入草稿，区分禁用保留与启用后按公共规则拒绝两种状态。 */
    private static void appendLegacyInput(JSONObject config, boolean enabled) {
        JSONObject legacy = new JSONObject().fluentPut("handler", HANDLER).fluentPut("mode", "input").fluentPut("name", "theadList")
                .fluentPut("defaultConfig", new JSONObject().fluentPut("enabled", enabled)
                        .fluentPut("value", JSON.parseArray("[{\"key\":\"legacy\",\"title\":\"旧输入字段\"}]")));
        JSONArray entries = JSON.parseArray(config.getJSONObject("param").getJSONArray("builtinDefaultList").toJSONString());
        entries.add(legacy);
        config.getJSONObject("param").put("builtinDefaultList", entries);
    }

    /** 只修改用例自己的默认配置草稿。 */
    private static JSONObject defaults(JSONObject config) {
        return config.getJSONObject("param").getJSONArray("builtinDefaultList").getJSONObject(0).getJSONObject("defaultConfig");
    }

    /** 比较字段集合时忽略 JSON 容器迭代顺序。 */
    private static Set<String> set(String... names) {
        return new HashSet<>(Arrays.asList(names));
    }

    /** 使用已带审计编号的成功结果，不连接任何真实接口。 */
    private static IntegrationResultVo successfulResponse() {
        IntegrationResultVo response = new IntegrationResultVo();
        response.setStatusCode(200);
        response.setAuditId(123L);
        response.setTransformedResult("{\"tbodyList\":[],\"currentPage\":1,\"pageSize\":20,\"rowNum\":0,\"pageCount\":0}");
        return response;
    }

    /** 仅替换真实网络调用，输出校验继续调用生产大屏规范。 */
    private static class RecordingIntegrationHandler extends DashboardIntegrationHandler {
        private int calls;
        private int sends;
        private int validations;
        private JSONObject parameters;
        private IRequestFrom requestFrom;
        private RuntimeException failure;
        private IntegrationResultVo response = successfulResponse();

        /** 子类替身沿用生产处理器标识，以复用固定参数默认配置。 */
        @Override public String getHandler() { return HANDLER; }

        /** 记录经过权限控制的实际执行输入与审计来源，禁止网络访问。 */
        @Override public IntegrationResultVo sendRequest(IntegrationVo source, IRequestFrom from) {
            calls++;
            // 发送替身执行基类已有输入准备，默认值只补齐一次，不要求生产适配器重复 apply。
            prepareInputParam(source);
            parameters = JSON.parseObject(source.getParamObj().toJSONString());
            requestFrom = from;
            // 在真实输入准备成功后才进入模拟发送，随后执行基类原有输出默认值补齐。
            sends++;
            if (failure != null) throw failure;
            IntegrationDefaultValueUtil.applyOutput(this, source.getConfig(), response);
            return response;
        }

        /** 记录调用次数并执行真实输出规范校验。 */
        @Override public void validate(IntegrationResultVo result) {
            validations++;
            super.validate(result);
        }
    }
}
