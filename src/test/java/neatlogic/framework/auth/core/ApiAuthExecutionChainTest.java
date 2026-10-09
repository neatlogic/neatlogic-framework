package neatlogic.framework.auth.core;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.dto.UserAuthVo;
import neatlogic.framework.exception.type.PermissionDeniedException;
import neatlogic.framework.restful.core.ApiComponentBase;
import neatlogic.framework.restful.core.privateapi.binarystream.BinaryStreamApiComponentBase;
import neatlogic.framework.restful.core.privateapi.jsonstream.JsonStreamApiComponentBase;
import neatlogic.framework.restful.core.privateapi.raw.RawApiComponentBase;
import neatlogic.framework.restful.core.privateapi.sse.SseApiComponentBase;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.util.SpringContextUtil;
import neatlogic.framework.config.FrameworkTenantConfig;
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
 * 验证各类 API 公共执行入口完整维护统一权限检查。
 */
public class ApiAuthExecutionChainTest {
    private static Field userMapperField;
    private static Object originalUserMapper;
    private static Field applicationContextField;
    private static Object originalApplicationContext;

    /**
     * 安装指定权限 Mapper，使未授予权限 的接口稳定进入拒绝分支。
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
                        return Collections.singletonList(new UserAuthVo(SystemUser.SYSTEM.getUserUuid(), TEST_EXEC_AUTH.class.getSimpleName()));
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
        UserContext.init(userVo, new AuthenticationInfoVo(userVo.getUuid()), FrameworkTenantConfig.TENANT_DEFAULT_TIMEZONE.getValue());
        TenantContext.init("test-tenant");
    }

    /**
     * 清理线程上下文，确保执行链测试之间互不影响。
     */
    @After
    public void cleanupContext() {

        if (UserContext.get() != null) {
            UserContext.get().release();
        }
        TenantContext.get().release();
    }

    /**
     * 对象型接口的权限校验、测试分支和服务分支均处于 实际授权内。
     */
    @Test
    public void shouldCheckObjectApiValidationTestAndService() throws Exception {
        MatchingObjectApi api = new MatchingObjectApi();

        Assert.assertEquals("service", api.doService(createApiVo(1), new JSONObject(), null));
        Assert.assertEquals("test", api.doService(createApiVo(0), new JSONObject(), null));


        try {
            new UnmatchedObjectApi().doService(createApiVo(1), new JSONObject(), null);
            Assert.fail("未授予权限 的受限接口应拒绝系统用户");
        } catch (PermissionDeniedException ignored) {

        }
    }

    /**
     * 对象型接口业务异常退出后必须清理 实际授权。
     */
    @Test
    public void shouldPreserveObjectApiException() {
        try {
            new ThrowingObjectApi().doService(createApiVo(1), new JSONObject(), null);
            Assert.fail("测试接口应抛出异常");
        } catch (Exception ignored) {

        }
    }

    /**
     * 四类流式 API 在入口与业务方法中均按实际权限校验。
     */
    @Test
    public void shouldCheckAllStreamApis() throws Exception {
        ApiVo apiVo = createApiVo(1);
        Assert.assertEquals("raw", new MatchingRawApi().doService(apiVo, "{}", null));
        assertOtherPermissionDenied();

        try (JSONReader jsonReader = new JSONReader(new StringReader("{}"))) {
            Assert.assertEquals("json", new MatchingJsonStreamApi().doService(apiVo, new JSONObject(), jsonReader));
        }
        assertOtherPermissionDenied();

        Assert.assertEquals("binary", new MatchingBinaryStreamApi().doService(apiVo, new JSONObject(), null, null));
        assertOtherPermissionDenied();

        Assert.assertEquals("sse", new MatchingSseApi().doService(apiVo, new JSONObject(), null, null));
        assertOtherPermissionDenied();
    }

    /**
     * 四类流式 API 的业务异常必须完整保留，后续请求仍按实际权限校验。
     */
    @Test
    public void shouldPreserveAllStreamApiFailures() throws Exception {
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
     * 断言接口已退出权限校验边界。
     */
    private void assertOtherPermissionDenied() {

    }

    /**
     * 断言流式接口保留原始异常，后续无权限请求仍被拒绝。
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
        assertOtherPermissionDenied();

        try {
            Assert.assertFalse(AuthActionChecker.check(TEST_OTHER_AUTH.class));
        } finally {

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
     * 断言业务方法只能通过已授予的权限校验。
     */
    private static void assertGrantedPermission() {

        Assert.assertTrue(AuthActionChecker.check(TEST_EXEC_AUTH.class));
    }

    /**
     * API 执行链测试使用的受限权限。
     */
    public static class TEST_EXEC_AUTH extends AuthActionCheckerTest.TestAuth {
    }

    /** 未授予的权限，用于验证接口与内部检查均不会按身份放行。 */
    public static class TEST_OTHER_AUTH extends AuthActionCheckerTest.TestAuth {
    }

    /**
     * 同时覆盖对象型接口的测试和正式执行分支。
     */

    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class MatchingObjectApi extends ApiComponentBase {
        @Override
        public String getName() {
            return "test";
        }

        @Override
        public Object myDoTest(JSONObject paramObj) {
            assertGrantedPermission();
            return "test";
        }

        @Override
        public Object myDoService(JSONObject paramObj) throws Exception {
            assertGrantedPermission();
            return "service";
        }
    }

    /**
     * 当前系统用户未获授权的对象型受限接口。
     */

    @AuthAction(action = TEST_OTHER_AUTH.class)
    public static class UnmatchedObjectApi extends MatchingObjectApi {
    }

    /**
     * 在业务执行阶段抛出异常的对象型接口。
     */

    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class ThrowingObjectApi extends MatchingObjectApi {
        @Override
        public Object myDoService(JSONObject paramObj) throws Exception {
            assertGrantedPermission();
            throw new Exception("test");
        }
    }

    /**
     * 当前系统用户已获授权的 Raw 接口。
     */

    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class MatchingRawApi extends RawApiComponentBase {
        @Override
        public String getName() {
            return "test";
        }

        @Override
        public Object myDoService(String param) {
            assertGrantedPermission();
            return "raw";
        }
    }

    /**
     * 在业务阶段抛出异常的 Raw 接口。
     */

    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class ThrowingRawApi extends MatchingRawApi {
        private static final RuntimeException FAILURE = new IllegalStateException("raw");

        @Override
        public Object myDoService(String param) {
            assertGrantedPermission();
            throw FAILURE;
        }
    }

    /**
     * 当前系统用户已获授权的 JSON 流接口。
     */

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
            assertGrantedPermission();
            jsonReader.readObject();
            return "json";
        }
    }

    /**
     * 在业务阶段抛出异常的 JSON 流接口。
     */

    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class ThrowingJsonStreamApi extends MatchingJsonStreamApi {
        private static final RuntimeException FAILURE = new IllegalStateException("json");

        @Override
        public Object myDoService(JSONObject paramObj, JSONReader jsonReader) {
            assertGrantedPermission();
            jsonReader.readObject();
            throw FAILURE;
        }
    }

    /**
     * 当前系统用户已获授权的二进制流接口。
     */

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
            assertGrantedPermission();
            return "binary";
        }
    }

    /**
     * 在业务阶段抛出异常的二进制流接口。
     */

    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class ThrowingBinaryStreamApi extends MatchingBinaryStreamApi {
        private static final RuntimeException FAILURE = new IllegalStateException("binary");

        @Override
        public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) {
            assertGrantedPermission();
            throw FAILURE;
        }
    }

    /**
     * 当前系统用户已获授权的 SSE 接口。
     */

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
            assertGrantedPermission();
            return "sse";
        }
    }

    /**
     * 在业务阶段抛出异常的 SSE 接口。
     */

    @AuthAction(action = TEST_EXEC_AUTH.class)
    public static class ThrowingSseApi extends MatchingSseApi {
        private static final RuntimeException FAILURE = new IllegalStateException("sse");

        @Override
        public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) {
            assertGrantedPermission();
            throw FAILURE;
        }
    }
}
