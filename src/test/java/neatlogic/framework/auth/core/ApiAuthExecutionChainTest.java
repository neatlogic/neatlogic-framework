package neatlogic.framework.auth.core;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.type.PermissionDeniedException;
import neatlogic.framework.restful.annotation.AuthUser;
import neatlogic.framework.restful.core.ApiComponentBase;
import neatlogic.framework.restful.core.privateapi.binarystream.BinaryStreamApiComponentBase;
import neatlogic.framework.restful.core.privateapi.jsonstream.JsonStreamApiComponentBase;
import neatlogic.framework.restful.core.privateapi.raw.RawApiComponentBase;
import neatlogic.framework.restful.core.privateapi.sse.SseApiComponentBase;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.util.SpringContextUtil;
import neatlogic.framework.util.TimeUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.StaticMessageSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Collections;

/**
 * 验证各类 API 公共执行入口完整维护系统用户鉴权作用域。
 */
public class ApiAuthExecutionChainTest {
    private static Field userMapperField;
    private static Object originalUserMapper;
    private static Field applicationContextField;
    private static Object originalApplicationContext;

    /**
     * 安装空权限 Mapper，使未匹配 AuthUser 的接口稳定进入拒绝分支。
     */
    @BeforeClass
    public static void installUserMapper() throws Exception {
        userMapperField = AuthActionChecker.class.getDeclaredField("userMapper");
        userMapperField.setAccessible(true);
        originalUserMapper = userMapperField.get(null);
        UserMapper userMapper = (UserMapper) Proxy.newProxyInstance(
                UserMapper.class.getClassLoader(),
                new Class[]{UserMapper.class},
                (proxy, method, args) -> {
                    if ("searchUserAllAuthByUserAuth".equals(method.getName())) {
                        return Collections.emptyList();
                    }
                    return null;
                });
        new AuthActionChecker().setUserMapper(userMapper);

        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.getBeanFactory().registerSingleton(
                "messageSourceAccessor", new MessageSourceAccessor(new StaticMessageSource()));
        applicationContextField = SpringContextUtil.class.getDeclaredField("ctx");
        applicationContextField.setAccessible(true);
        originalApplicationContext = applicationContextField.get(null);
        applicationContextField.set(null, applicationContext);
    }

    /**
     * 恢复测试前的静态 Mapper。
     */
    @AfterClass
    public static void restoreUserMapper() throws Exception {
        userMapperField.set(null, originalUserMapper);
        applicationContextField.set(null, originalApplicationContext);
    }

    /**
     * 为每个用例建立系统用户和普通租户上下文。
     */
    @Before
    public void initContext() {
        UserVo userVo = new UserVo();
        userVo.setUuid(SystemUser.SYSTEM.getUserUuid());
        userVo.setUserId(SystemUser.SYSTEM.getUserId());
        userVo.setAuthorization("test-authorization");
        userVo.setIsSuperAdmin(false);
        UserContext.init(userVo, new AuthenticationInfoVo(userVo.getUuid()), TimeUtil.ZONE_TIME);
        TenantContext.init("test-tenant");
    }

    /**
     * 清理线程上下文，确保执行链测试之间互不影响。
     */
    @After
    public void cleanupContext() {
        ApiAuthContext.exit();
        if (UserContext.get() != null) {
            UserContext.get().release();
        }
        TenantContext.get().release();
    }

    /**
     * 对象型接口的权限校验、测试分支和服务分支均处于 AuthUser 作用域内。
     */
    @Test
    public void shouldApplyScopeToObjectApiValidationTestAndService() throws Exception {
        MatchingObjectApi api = new MatchingObjectApi();

        Assert.assertEquals("service", api.doService(createApiVo(1), new JSONObject(), null));
        Assert.assertEquals("test", api.doService(createApiVo(0), new JSONObject(), null));
        Assert.assertFalse(ApiAuthContext.isCurrentSystemUserExempt());

        try {
            new UnmatchedObjectApi().doService(createApiVo(1), new JSONObject(), null);
            Assert.fail("未匹配 AuthUser 的受限接口应拒绝系统用户");
        } catch (PermissionDeniedException ignored) {
            Assert.assertFalse(ApiAuthContext.isCurrentSystemUserExempt());
        }
    }

    /**
     * 对象型接口业务异常退出后必须清理 AuthUser 作用域。
     */
    @Test
    public void shouldCleanupObjectApiScopeAfterException() {
        try {
            new ThrowingObjectApi().doService(createApiVo(1), new JSONObject(), null);
            Assert.fail("测试接口应抛出异常");
        } catch (Exception ignored) {
            Assert.assertFalse(ApiAuthContext.isCurrentSystemUserExempt());
        }
    }

    /**
     * 四类流式 API 在业务执行期间均应建立作用域，并在返回后清理。
     */
    @Test
    public void shouldApplyAndCleanupScopeForAllStreamApis() throws Exception {
        ApiVo apiVo = createApiVo(1);
        Assert.assertEquals("raw", new MatchingRawApi().doService(apiVo, "{}", null));
        assertScopeCleared();

        try (JSONReader jsonReader = new JSONReader(new StringReader("{}"))) {
            Assert.assertEquals("json", new MatchingJsonStreamApi().doService(apiVo, new JSONObject(), jsonReader));
        }
        assertScopeCleared();

        Assert.assertEquals("binary", new MatchingBinaryStreamApi().doService(apiVo, new JSONObject(), null, null));
        assertScopeCleared();

        Assert.assertEquals("sse", new MatchingSseApi().doService(apiVo, new JSONObject(), null, null));
        assertScopeCleared();
    }

    /**
     * 四类流式 API 的业务异常必须完整保留，并在异常后清理鉴权作用域。
     */
    @Test
    public void shouldCleanupScopeAfterAllStreamApiFailures() throws Exception {
        ApiVo apiVo = createApiVo(1);
        assertStreamFailure(ThrowingRawApi.FAILURE,
                () -> new ThrowingRawApi().doService(apiVo, "{}", null));

        try (JSONReader jsonReader = new JSONReader(new StringReader("{}"))) {
            assertStreamFailure(ThrowingJsonStreamApi.FAILURE,
                    () -> new ThrowingJsonStreamApi().doService(apiVo, new JSONObject(), jsonReader));
        }

        assertStreamFailure(ThrowingBinaryStreamApi.FAILURE,
                () -> new ThrowingBinaryStreamApi().doService(apiVo, new JSONObject(), null, createResponse()));
        assertStreamFailure(ThrowingSseApi.FAILURE,
                () -> new ThrowingSseApi().doService(apiVo, new JSONObject(), null, null));
    }

    /**
     * 创建执行测试所需的最小 API 元数据。
     */
    private ApiVo createApiVo(int isActive) {
        ApiVo apiVo = new ApiVo();
        apiVo.setIsActive(isActive);
        apiVo.setToken("test.token");
        apiVo.setModuleId("framework");
        apiVo.setNeedAudit(0);
        return apiVo;
    }

    /**
     * 断言接口已退出系统用户豁免作用域。
     */
    private void assertScopeCleared() {
        Assert.assertFalse(ApiAuthContext.isCurrentSystemUserExempt());
    }

    /**
     * 断言流式接口保留原始异常，并验证后续不匹配作用域不会受到残留状态污染。
     */
    private void assertStreamFailure(RuntimeException expected, StreamInvocation invocation) throws Exception {
        try {
            invocation.execute();
            Assert.fail("流式接口应抛出测试异常");
        } catch (Exception actual) {
            Throwable target = actual;
            while (target != expected && target.getCause() != null) {
                target = target.getCause();
            }
            Assert.assertSame(expected, target);
        }
        assertScopeCleared();
        ApiAuthContext.enter(UnmatchedObjectApi.class);
        try {
            Assert.assertFalse(AuthActionChecker.check(TEST_EXEC_AUTH.class));
        } finally {
            ApiAuthContext.exit();
        }
    }

    /**
     * 创建仅用于接收响应头的最小 HTTP 响应代理。
     */
    private HttpServletResponse createResponse() {
        return (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class[]{HttpServletResponse.class},
                (proxy, method, args) -> null);
    }

    /**
     * 支持抛出异常的流式接口调用。
     */
    @FunctionalInterface
    private interface StreamInvocation {
        /**
         * 执行一次流式接口调用。
         */
        Object execute() throws Exception;
    }

    /**
     * 断言当前业务方法运行在匹配的系统用户豁免作用域内。
     */
    private static void assertScopeActive() {
        Assert.assertTrue(ApiAuthContext.isCurrentSystemUserExempt());
        Assert.assertTrue(AuthActionChecker.check(TEST_EXEC_AUTH.class));
    }

    /**
     * API 执行链测试使用的受限权限。
     */
    public static class TEST_EXEC_AUTH extends ApiAuthContextTest.TestAuth {
    }

    /**
     * 同时覆盖对象型接口的测试和正式执行分支。
     */
    @AuthUser(SystemUser.SYSTEM)
    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class MatchingObjectApi extends ApiComponentBase {
        @Override
        public String getName() {
            return "test";
        }

        @Override
        public Object myDoTest(JSONObject paramObj) {
            assertScopeActive();
            return "test";
        }

        @Override
        public Object myDoService(JSONObject paramObj) throws Exception {
            assertScopeActive();
            return "service";
        }
    }

    /**
     * 未匹配当前系统用户的对象型受限接口。
     */
    @AuthUser(SystemUser.AUTOEXEC)
    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class UnmatchedObjectApi extends MatchingObjectApi {
    }

    /**
     * 在业务执行阶段抛出异常的对象型接口。
     */
    @AuthUser(SystemUser.SYSTEM)
    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class ThrowingObjectApi extends MatchingObjectApi {
        @Override
        public Object myDoService(JSONObject paramObj) throws Exception {
            assertScopeActive();
            throw new Exception("test");
        }
    }

    /**
     * 匹配当前系统用户的 Raw 接口。
     */
    @AuthUser(SystemUser.SYSTEM)
    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class MatchingRawApi extends RawApiComponentBase {
        @Override
        public String getName() {
            return "test";
        }

        @Override
        public Object myDoService(String param) {
            assertScopeActive();
            return "raw";
        }
    }

    /**
     * 在业务阶段抛出异常的 Raw 接口。
     */
    @AuthUser(SystemUser.SYSTEM)
    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class ThrowingRawApi extends MatchingRawApi {
        private static final RuntimeException FAILURE = new IllegalStateException("raw");

        @Override
        public Object myDoService(String param) {
            assertScopeActive();
            throw FAILURE;
        }
    }

    /**
     * 匹配当前系统用户的 JSON 流接口。
     */
    @AuthUser(SystemUser.SYSTEM)
    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class MatchingJsonStreamApi extends JsonStreamApiComponentBase {
        @Override
        public String getName() {
            return "test";
        }

        @Override
        public String getConfig() {
            return null;
        }

        @Override
        public Object myDoService(JSONObject paramObj, JSONReader jsonReader) {
            assertScopeActive();
            jsonReader.readObject();
            return "json";
        }
    }

    /**
     * 在业务阶段抛出异常的 JSON 流接口。
     */
    @AuthUser(SystemUser.SYSTEM)
    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class ThrowingJsonStreamApi extends MatchingJsonStreamApi {
        private static final RuntimeException FAILURE = new IllegalStateException("json");

        @Override
        public Object myDoService(JSONObject paramObj, JSONReader jsonReader) {
            assertScopeActive();
            jsonReader.readObject();
            throw FAILURE;
        }
    }

    /**
     * 匹配当前系统用户的二进制流接口。
     */
    @AuthUser(SystemUser.SYSTEM)
    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class MatchingBinaryStreamApi extends BinaryStreamApiComponentBase {
        @Override
        public String getName() {
            return "test";
        }

        @Override
        public String getConfig() {
            return null;
        }

        @Override
        public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) {
            assertScopeActive();
            return "binary";
        }
    }

    /**
     * 在业务阶段抛出异常的二进制流接口。
     */
    @AuthUser(SystemUser.SYSTEM)
    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class ThrowingBinaryStreamApi extends MatchingBinaryStreamApi {
        private static final RuntimeException FAILURE = new IllegalStateException("binary");

        @Override
        public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) {
            assertScopeActive();
            throw FAILURE;
        }
    }

    /**
     * 匹配当前系统用户的 SSE 接口。
     */
    @AuthUser(SystemUser.SYSTEM)
    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class MatchingSseApi extends SseApiComponentBase {
        @Override
        public String getToken() {
            return "test.token";
        }

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) {
            assertScopeActive();
            return "sse";
        }
    }

    /**
     * 在业务阶段抛出异常的 SSE 接口。
     */
    @AuthUser(SystemUser.SYSTEM)
    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class ThrowingSseApi extends MatchingSseApi {
        private static final RuntimeException FAILURE = new IllegalStateException("sse");

        @Override
        public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) {
            assertScopeActive();
            throw FAILURE;
        }
    }
}
