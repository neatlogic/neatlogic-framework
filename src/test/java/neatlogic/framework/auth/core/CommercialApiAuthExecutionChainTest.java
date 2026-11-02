package neatlogic.framework.auth.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.label.NoAuth;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.config.FrameworkTenantConfig;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.UserAuthVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.type.PermissionDeniedException;
import neatlogic.framework.restful.core.ApiComponentBase;
import neatlogic.framework.restful.core.privateapi.binarystream.BinaryStreamApiComponentBase;
import neatlogic.framework.restful.core.privateapi.jsonstream.JsonStreamApiComponentBase;
import neatlogic.framework.restful.core.privateapi.raw.RawApiComponentBase;
import neatlogic.framework.restful.core.privateapi.sse.SseApiComponentBase;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.util.SpringContextUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/** 用真实公共执行链验证商业授权先于历史权限与特殊身份，不连接数据库。 */
public class CommercialApiAuthExecutionChainTest {
    private static final String MODULE = "commercial-api-chain-test";
    private static final String USER = "commercial-api-chain-user";
    private static final AtomicInteger EXECUTIONS = new AtomicInteger();
    private final List<UserAuthVo> storedAuths = new ArrayList<>();
    private final Map<String, AuthBase> previousAuths = new HashMap<>();
    private Map<String, AuthBase> authMap;
    private Set<String> invalidModules;
    private boolean previouslyInvalid;
    private Object previousMapper;
    private Object previousContext;

    /** 注入最小用户权限及语言环境，临时注册测试权限并记录原状态。 */
    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        authMap = (Map<String, AuthBase>) field(AuthFactory.class, "authMap").get(null);
        invalidModules = (Set<String>) field(ModuleUtil.class, "invalidatedModuleSet").get(null);
        previouslyInvalid = invalidModules.remove(MODULE);
        for (AuthBase auth : Arrays.asList(new CHAIN_COMMERCIAL_AUTH(), new CHAIN_FREE_AUTH(), new CHAIN_OTHER_COMMERCIAL_AUTH())) {
            previousAuths.put(auth.getAuthName(), authMap.put(auth.getAuthName(), auth));
        }
        previousMapper = field(AuthActionChecker.class, "userMapper").get(null);
        UserMapper mapper = (UserMapper) Proxy.newProxyInstance(UserMapper.class.getClassLoader(),
                new Class<?>[]{UserMapper.class}, (proxy, method, args) -> {
                    if ("searchUserAllAuthByUserAuth".equals(method.getName())) return new ArrayList<>(storedAuths);
                    return null;
                });
        new AuthActionChecker().setUserMapper(mapper);
        previousContext = field(SpringContextUtil.class, "ctx").get(null);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("messageSourceAccessor",
                new MessageSourceAccessor(new StaticMessageSource()));
        field(SpringContextUtil.class, "ctx").set(null, context);
        storedAuths.add(new UserAuthVo(USER, CHAIN_COMMERCIAL_AUTH.class.getSimpleName()));
        TenantContext.init("commercial-api-chain-tenant");
        initUser(false);
        EXECUTIONS.set(0);
    }

    /** 还原静态注册、依赖和上下文，保证本测试不改变其他权限场景。 */
    @After
    public void cleanup() throws Exception {
        for (Map.Entry<String, AuthBase> entry : previousAuths.entrySet()) {
            if (entry.getValue() == null) authMap.remove(entry.getKey());
            else authMap.put(entry.getKey(), entry.getValue());
        }
        if (previouslyInvalid) invalidModules.add(MODULE);
        else invalidModules.remove(MODULE);
        field(AuthActionChecker.class, "userMapper").set(null, previousMapper);
        field(SpringContextUtil.class, "ctx").set(null, previousContext);
        if (UserContext.get() != null) UserContext.get().release();
        if (TenantContext.get() != null) TenantContext.get().release();
    }

    /** 模块失效或标签未注册时，历史数据库授权不能进入任何一种业务方法。 */
    @Test
    public void rejectsHistoricalGrantAcrossAllApiKinds() throws Exception {
        invalidModules.add(MODULE);
        assertAllDenied();
        invalidModules.remove(MODULE);
        authMap.remove(CHAIN_COMMERCIAL_AUTH.class.getSimpleName());
        assertAllDenied();
        assertEquals(0, EXECUTIONS.get());
    }

    /** 超级管理员也不能通过普通租户的商业权限检查。 */
    @Test
    public void rejectsSuperAdminBeforeExecutingCommercialApi() throws Exception {
        initUser(true);
        invalidModules.add(MODULE);
        assertAllDenied();
        assertEquals(0, EXECUTIONS.get());
    }

    /** 商业授权有效后仍需实际用户授权，原有接口返回契约保持不变。 */
    @Test
    public void requiresBothValidLicenseAndActualUserGrant() throws Exception {
        assertAllAllowed();
        assertEquals(5, EXECUTIONS.get());
        storedAuths.clear();
        assertAllDenied();
        assertEquals(5, EXECUTIONS.get());
    }

    /** master 只保留 API 层的原有豁免，不要求业务方法绕过统一检查器。 */
    @Test
    public void preservesExistingMasterApiExemption() throws Exception {
        TenantContext.get().release();
        TenantContext.init("master");
        invalidModules.add(MODULE);
        storedAuths.clear();
        assertAllAllowed();
        assertEquals(5, EXECUTIONS.get());
    }

    /** 失效商业权限只是不可用的候选，其他有效权限及明确 NoAuth 仍可任选其一。 */
    @Test
    public void preservesAlternativeFreeAndNoAuthPermissions() throws Exception {
        invalidModules.add(MODULE);
        assertDenied(() -> new AlternativeObjectApi().doService(api(), new JSONObject(), null));
        assertDenied(() -> new AlternativeCommercialObjectApi().doService(api(), new JSONObject(), null));
        storedAuths.add(new UserAuthVo(USER, CHAIN_OTHER_COMMERCIAL_AUTH.class.getSimpleName()));
        assertEquals("licensed-alternative", new AlternativeCommercialObjectApi().doService(api(), new JSONObject(), null));
        storedAuths.clear();
        storedAuths.add(new UserAuthVo(USER, CHAIN_FREE_AUTH.class.getSimpleName()));
        assertEquals("alternative", new AlternativeObjectApi().doService(api(), new JSONObject(), null));
        storedAuths.clear();
        assertEquals("public", new PublicObjectApi().doService(api(), new JSONObject(), null));
        assertEquals(3, EXECUTIONS.get());
    }

    /** 通过授权后业务异常仍保留原始对象，不被改造成权限拒绝。 */
    @Test
    public void preservesBusinessFailureAfterAuthorization() throws Exception {
        try {
            new ThrowingObjectApi().doService(api(), new JSONObject(), null);
            fail("应保留业务异常");
        } catch (Exception actual) {
            assertSame(ThrowingObjectApi.FAILURE, actual);
        }
    }

    /** 所有类型的接口在普通租户和 master 都不能使用未声明模块的标签。 */
    @Test
    public void rejectsUndeclaredLabelsAcrossApiKindsAndMaster() throws Exception {
        initUser(true);
        storedAuths.add(new UserAuthVo(USER, UNDECLARED_CHAIN_AUTH.class.getSimpleName()));
        for (String tenant : Arrays.asList("commercial-api-chain-tenant", "master")) {
            TenantContext.get().release();
            TenantContext.init(tenant);
            assertDenied(() -> new UndeclaredObjectApi().doService(api(), new JSONObject(), null));
            assertDenied(() -> new UndeclaredRawApi().doService(api(), "{}", null));
            try (JSONReader reader = new JSONReader(new StringReader("{}"))) {
                reader.readObject();
                assertDenied(() -> new UndeclaredJsonApi().doService(api(), new JSONObject(), reader));
            }
            assertDenied(() -> new UndeclaredBinaryApi().doService(api(), new JSONObject(), null, new MockHttpServletResponse()));
            assertDenied(() -> new UndeclaredSseApi().doService(api(), new JSONObject(), null, null));
        }
        assertEquals(0, EXECUTIONS.get());
    }

    /** 合法 NoAuth 可以作为未声明标签之外的另一条访问路径。 */
    @Test
    public void preservesDeclaredNoAuthAlternativeForUndeclaredLabel() throws Exception {
        assertEquals("public-alternative", new UndeclaredPublicAlternativeApi().doService(api(), new JSONObject(), null));
    }

    /** 故意省略模块声明，不能继承商业父类注解。 */
    public static class UNDECLARED_CHAIN_AUTH extends CHAIN_COMMERCIAL_AUTH { }

    /** 验证对象入口未声明时拒绝。 */
    @AuthAction(action = UNDECLARED_CHAIN_AUTH.class)
    public static class UndeclaredObjectApi extends CommercialObjectApi { }
    /** 验证原始流入口未声明时拒绝。 */
    @AuthAction(action = UNDECLARED_CHAIN_AUTH.class)
    public static class UndeclaredRawApi extends CommercialRawApi { }
    /** 验证 JSON 流入口未声明时拒绝。 */
    @AuthAction(action = UNDECLARED_CHAIN_AUTH.class)
    public static class UndeclaredJsonApi extends CommercialJsonApi { }
    /** 验证二进制入口未声明时拒绝。 */
    @AuthAction(action = UNDECLARED_CHAIN_AUTH.class)
    public static class UndeclaredBinaryApi extends CommercialBinaryApi { }
    /** 验证 SSE 入口未声明时拒绝。 */
    @AuthAction(action = UNDECLARED_CHAIN_AUTH.class)
    public static class UndeclaredSseApi extends CommercialSseApi { }

    /** 未声明标签不生效，但其他声明合法的候选仍可任选其一。 */
    @AuthAction(action = UNDECLARED_CHAIN_AUTH.class)
    @AuthAction(action = NoAuth.class)
    public static class UndeclaredPublicAlternativeApi extends ApiComponentBase {
        /** 返回测试名称。 */
        @Override public String getName() { return "test"; }
        /** 记录允许候选命中的业务执行。 */
        @Override public Object myDoService(JSONObject input) { return executed("public-alternative"); }
    }

    /** 所有执行入口必须在调用业务方法前拒绝访问。 */
    private void assertAllDenied() throws Exception {
        assertDenied(() -> new CommercialObjectApi().doService(api(), new JSONObject(), null));
        assertDenied(() -> new CommercialRawApi().doService(api(), "{}", null));
        try (JSONReader reader = new JSONReader(new StringReader("{}"))) {
            // 拒绝场景业务不会消费流，先耗尽测试输入以便正常关闭读取器。
            reader.readObject();
            assertDenied(() -> new CommercialJsonApi().doService(api(), new JSONObject(), reader));
        }
        assertDenied(() -> new CommercialBinaryApi().doService(api(), new JSONObject(), null, new MockHttpServletResponse()));
        assertDenied(() -> new CommercialSseApi().doService(api(), new JSONObject(), null, null));
    }

    /** 对象、原始文本、JSON 流、二进制和 SSE 均走各自生产执行入口。 */
    private void assertAllAllowed() throws Exception {
        assertEquals("object", new CommercialObjectApi().doService(api(), new JSONObject(), null));
        assertEquals("raw", new CommercialRawApi().doService(api(), "{}", null));
        try (JSONReader reader = new JSONReader(new StringReader("{}"))) {
            assertEquals("json", new CommercialJsonApi().doService(api(), new JSONObject(), reader));
        }
        assertEquals("binary", new CommercialBinaryApi().doService(api(), new JSONObject(), null, new MockHttpServletResponse()));
        assertEquals("sse", new CommercialSseApi().doService(api(), new JSONObject(), null, null));
    }

    /** 维持现有权限拒绝异常类型，其他异常不能被视为成功拒绝。 */
    private void assertDenied(Invocation invocation) throws Exception {
        try {
            invocation.execute();
            fail("失效商业权限不应进入业务方法");
        } catch (PermissionDeniedException expected) {
            // 真实公共执行入口沿用框架权限拒绝响应。
        }
    }

    /** 创建活动接口的最小元数据，关闭与授权无关的审计副作用。 */
    private ApiVo api() {
        ApiVo api = new ApiVo();
        api.setIsActive(1);
        api.setNeedAudit(0);
        api.setModuleId("framework");
        api.setToken("commercial.chain.test");
        return api;
    }

    /** 使用真实普通用户上下文切换超级管理员标记。 */
    private void initUser(boolean superAdmin) {
        if (UserContext.get() != null) UserContext.get().release();
        UserVo user = new UserVo();
        user.setUuid(USER);
        user.setUserId(USER);
        user.setIsSuperAdmin(superAdmin);
        user.setAuthorization("test-authorization");
        UserContext.init(user, new AuthenticationInfoVo(USER), FrameworkTenantConfig.TENANT_DEFAULT_TIMEZONE.getValue());
    }

    /** 反射仅用于测试替身注入，不向生产代码添加装配接口。 */
    private static Field field(Class<?> type, String name) throws Exception {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    /** 记录真实业务执行次数，使提前拒绝具有可验证的副作用边界。 */
    private static String executed(String result) { EXECUTIONS.incrementAndGet(); return result; }

    /** 表示一次允许抛出异常的真实 API 调用。 */
    private interface Invocation { Object execute() throws Exception; }

    /** 测试商业权限明确声明所属模块，避免测试包路径影响判断。 */
    @AuthModule("commercial-api-chain-test")
    public static class CHAIN_COMMERCIAL_AUTH extends AuthCSBase {
        /** 返回固定测试模块。 */
            /** 返回测试权限显示键。 */
        @Override public String getAuthDisplayName() { return "test.commercial"; }
        /** 返回测试权限说明键。 */
        @Override public String getAuthIntroduction() { return "test.commercial"; }
        /** 返回固定测试权限组。 */
        @Override public String getAuthGroup() { return MODULE; }
        /** 返回稳定排序。 */
        @Override public Integer getSort() { return 1; }
    }

    /** 另一个授权有效的商业模块，验证商业权限之间也保留任选其一。 */
    @AuthModule("other-commercial-api-chain-test")
    public static class CHAIN_OTHER_COMMERCIAL_AUTH extends CHAIN_COMMERCIAL_AUTH {
        /** 返回另一个固定测试模块。 */
        }

    /** 测试非商业替代权限。 */
    @AuthModule("framework")
    public static class CHAIN_FREE_AUTH extends AuthBase {
        /** 返回测试权限显示键。 */
        @Override public String getAuthDisplayName() { return "test.free"; }
        /** 返回测试权限说明键。 */
        @Override public String getAuthIntroduction() { return "test.free"; }
        /** 返回固定测试权限组。 */
        @Override public String getAuthGroup() { return MODULE; }
        /** 返回稳定排序。 */
        @Override public Integer getSort() { return 2; }
    }

    /** 对象型商业接口。 */
    @AuthAction(action = CHAIN_COMMERCIAL_AUTH.class)
    public static class CommercialObjectApi extends ApiComponentBase {
        /** 返回测试名称。 */
        @Override public String getName() { return "test"; }
        /** 记录业务执行。 */
        @Override public Object myDoService(JSONObject input) { return executed("object"); }
    }

    /** 原始文本商业接口。 */
    @AuthAction(action = CHAIN_COMMERCIAL_AUTH.class)
    public static class CommercialRawApi extends RawApiComponentBase {
        /** 返回测试名称。 */
        @Override public String getName() { return "test"; }
        /** 记录业务执行。 */
        @Override public Object myDoService(String input) { return executed("raw"); }
    }

    /** JSON 流商业接口。 */
    @AuthAction(action = CHAIN_COMMERCIAL_AUTH.class)
    public static class CommercialJsonApi extends JsonStreamApiComponentBase {
        /** 返回测试名称。 */
        @Override public String getName() { return "test"; }
        /** 无额外配置。 */
        @Override public String getConfig() { return null; }
        /** 记录业务执行。 */
        @Override public Object myDoService(JSONObject input, JSONReader reader) {
            reader.readObject();
            return executed("json");
        }
    }

    /** 二进制商业接口。 */
    @AuthAction(action = CHAIN_COMMERCIAL_AUTH.class)
    public static class CommercialBinaryApi extends BinaryStreamApiComponentBase {
        /** 返回测试名称。 */
        @Override public String getName() { return "test"; }
        /** 无额外配置。 */
        @Override public String getConfig() { return null; }
        /** 记录业务执行。 */
        @Override public Object myDoService(JSONObject input, HttpServletRequest request, HttpServletResponse response) { return executed("binary"); }
    }

    /** SSE 商业接口。 */
    @AuthAction(action = CHAIN_COMMERCIAL_AUTH.class)
    public static class CommercialSseApi extends SseApiComponentBase {
        /** 返回固定接口令牌。 */
        @Override public String getToken() { return "commercial.chain.test"; }
        /** 返回测试名称。 */
        @Override public String getName() { return "test"; }
        /** 记录业务执行。 */
        @Override public Object myDoService(JSONObject input, HttpServletRequest request, HttpServletResponse response) { return executed("sse"); }
    }

    /** 同时允许商业权限或普通权限的对象接口。 */
    @AuthAction(action = CHAIN_COMMERCIAL_AUTH.class)
    @AuthAction(action = CHAIN_FREE_AUTH.class)
    public static class AlternativeObjectApi extends ApiComponentBase {
        /** 返回测试名称。 */
        @Override public String getName() { return "test"; }
        /** 记录业务执行。 */
        @Override public Object myDoService(JSONObject input) { return executed("alternative"); }
    }

    /** 允许另一个已获有效授权的商业权限作为替代。 */
    @AuthAction(action = CHAIN_COMMERCIAL_AUTH.class)
    @AuthAction(action = CHAIN_OTHER_COMMERCIAL_AUTH.class)
    public static class AlternativeCommercialObjectApi extends ApiComponentBase {
        /** 返回测试名称。 */
        @Override public String getName() { return "test"; }
        /** 记录业务执行。 */
        @Override public Object myDoService(JSONObject input) { return executed("licensed-alternative"); }
    }

    /** 明确允许无用户授权访问的对象接口。 */
    @AuthAction(action = CHAIN_COMMERCIAL_AUTH.class)
    @AuthAction(action = NoAuth.class)
    public static class PublicObjectApi extends ApiComponentBase {
        /** 返回测试名称。 */
        @Override public String getName() { return "test"; }
        /** 记录业务执行。 */
        @Override public Object myDoService(JSONObject input) { return executed("public"); }
    }

    /** 在商业授权通过后抛出业务异常的对象接口。 */
    @AuthAction(action = CHAIN_COMMERCIAL_AUTH.class)
    public static class ThrowingObjectApi extends ApiComponentBase {
        private static final RuntimeException FAILURE = new IllegalStateException("commercial-business-failure");
        /** 返回测试名称。 */
        @Override public String getName() { return "test"; }
        /** 抛出原始业务异常。 */
        @Override public Object myDoService(JSONObject input) { throw FAILURE; }
    }
}
