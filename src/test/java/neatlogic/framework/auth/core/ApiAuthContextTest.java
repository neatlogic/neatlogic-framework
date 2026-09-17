package neatlogic.framework.auth.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.init.MaintenanceMode;
import neatlogic.framework.auth.label.NoAuth;
import neatlogic.framework.auth.label.USER_MODIFY;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.UserAuthVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.restful.annotation.AuthUser;
import neatlogic.framework.restful.core.ApiComponentTemplateBase;
import neatlogic.framework.service.AuthenticationInfoService;
import neatlogic.framework.util.TimeUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 验证同步 API 系统用户豁免及指定用户权限校验边界。
 */
public class ApiAuthContextTest {
    private static Field userMapperField;
    private static Object originalUserMapper;
    private static Field authenticationInfoServiceField;
    private static Object originalAuthenticationInfoService;
    private static List<UserAuthVo> userAuthList = Collections.emptyList();
    private static final Map<String, List<UserAuthVo>> USER_AUTH_MAP = new HashMap<>();
    private static final Map<String, UserVo> USER_MAP = new HashMap<>();
    private static final AtomicInteger AUTHENTICATION_INFO_QUERY_COUNT = new AtomicInteger();
    private static final AtomicInteger USER_QUERY_COUNT = new AtomicInteger();
    private static final AtomicInteger AUTHORIZATION_QUERY_COUNT = new AtomicInteger();

    /**
     * 安装不返回任何权限的 UserMapper，隔离数据库并使未豁免路径稳定返回无权限。
     */
    @BeforeClass
    public static void installDependencies() throws Exception {
        userMapperField = AuthActionChecker.class.getDeclaredField("userMapper");
        userMapperField.setAccessible(true);
        originalUserMapper = userMapperField.get(null);
        UserMapper userMapper = (UserMapper) Proxy.newProxyInstance(
                UserMapper.class.getClassLoader(),
                new Class[]{UserMapper.class},
                (proxy, method, args) -> {
                    if ("searchUserAllAuthByUserAuth".equals(method.getName())) {
                        AUTHORIZATION_QUERY_COUNT.incrementAndGet();
                        AuthenticationInfoVo authenticationInfoVo = (AuthenticationInfoVo) args[0];
                        if (USER_AUTH_MAP.containsKey(authenticationInfoVo.getUserUuid())) {
                            return USER_AUTH_MAP.get(authenticationInfoVo.getUserUuid());
                        }
                        return userAuthList;
                    }
                    if ("getUserBaseInfoByUuidWithoutCache".equals(method.getName())) {
                        USER_QUERY_COUNT.incrementAndGet();
                        return USER_MAP.get(args[0]);
                    }
                    return null;
                }
        );
        new AuthActionChecker().setUserMapper(userMapper);

        authenticationInfoServiceField = AuthActionChecker.class.getDeclaredField("authenticationInfoService");
        authenticationInfoServiceField.setAccessible(true);
        originalAuthenticationInfoService = authenticationInfoServiceField.get(null);
        AuthenticationInfoService authenticationInfoService = (AuthenticationInfoService) Proxy.newProxyInstance(
                AuthenticationInfoService.class.getClassLoader(),
                new Class[]{AuthenticationInfoService.class},
                (proxy, method, args) -> {
                    if ("getAuthenticationInfo".equals(method.getName()) && args[0] instanceof String) {
                        AUTHENTICATION_INFO_QUERY_COUNT.incrementAndGet();
                        return new AuthenticationInfoVo((String) args[0]);
                    }
                    return null;
                }
        );
        new AuthActionChecker().setAuthenticationInfoService(authenticationInfoService);
    }

    /**
     * 每个用例默认从无权限状态开始。
     */
    @Before
    public void resetUserAuthList() {
        userAuthList = Collections.emptyList();
        USER_AUTH_MAP.clear();
        USER_MAP.clear();
        AUTHENTICATION_INFO_QUERY_COUNT.set(0);
        USER_QUERY_COUNT.set(0);
        AUTHORIZATION_QUERY_COUNT.set(0);
    }

    /**
     * 恢复静态依赖，避免影响同一测试进程中的其他用例。
     */
    @AfterClass
    public static void restoreDependencies() throws Exception {
        userMapperField.set(null, originalUserMapper);
        authenticationInfoServiceField.set(null, originalAuthenticationInfoService);
    }

    /**
     * 清理每个用例的线程上下文，避免失败用例污染后续断言。
     */
    @After
    public void cleanupContext() {
        ApiAuthContext.exit();
        ApiAuthContext.exit();
        if (UserContext.get() != null) {
            UserContext.get().release();
        }
        TenantContext.get().release();
    }

    /**
     * 未声明当前系统用户豁免时，所有公开校验入口都必须进入实际权限判断。
     */
    @Test
    public void shouldRequireAuthForSystemUserWithoutMatchingAuthUser() {
        initUser(SystemUser.SYSTEM.getUserUuid());
        ApiAuthContext.enter(OtherSystemUserApi.class);

        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));
        Assert.assertFalse(AuthActionChecker.check(SystemUser.SYSTEM.getUserUuid(), TestAuth.class));
    }

    /**
     * 未豁免的系统用户拥有目标权限时仍可正常通过。
     */
    @Test
    public void shouldAllowSystemUserWithGrantedAuth() {
        initUser(SystemUser.SYSTEM.getUserUuid());
        userAuthList = Collections.singletonList(new UserAuthVo(SystemUser.SYSTEM.getUserUuid(), TestAuth.class.getSimpleName()));
        ApiAuthContext.enter(OtherSystemUserApi.class);

        Assert.assertTrue(AuthActionChecker.check(TestAuth.class));
        Assert.assertTrue(AuthActionChecker.check(SystemUser.SYSTEM.getUserUuid(), TestAuth.class));
    }

    /**
     * 接口显式匹配当前系统用户时，入口及同步业务内部校验均允许豁免。
     */
    @Test
    public void shouldBypassAuthForMatchingSystemUser() {
        initUser(SystemUser.SYSTEM.getUserUuid());
        ApiAuthContext.enter(SystemUserApi.class);

        Assert.assertTrue(AuthActionChecker.check(TestAuth.class));
        Assert.assertTrue(AuthActionChecker.check(SystemUser.SYSTEM.getUserUuid(), TestAuth.class));
        Assert.assertFalse(AuthActionChecker.check(SystemUser.AUTOEXEC.getUserUuid(), TestAuth.class));
    }

    /**
     * AuthUser 不向普通用户授予权限。
     */
    @Test
    public void shouldNotApplyAuthUserToNormalUser() {
        initUser("normal-user");
        ApiAuthContext.enter(SystemUserApi.class);

        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));
    }

    /**
     * 嵌套 API 必须使用内层声明，并在退出后恢复外层豁免。
     */
    @Test
    public void shouldRestoreOuterScopeAfterNestedApi() {
        initUser(SystemUser.SYSTEM.getUserUuid());
        ApiAuthContext.enter(SystemUserApi.class);
        Assert.assertTrue(AuthActionChecker.check(TestAuth.class));

        ApiAuthContext.enter(OtherSystemUserApi.class);
        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));
        ApiAuthContext.exit();

        Assert.assertTrue(AuthActionChecker.check(TestAuth.class));
    }

    /**
     * 重复声明的 AuthUser 任一匹配即可豁免，内层不匹配作用域退出后必须恢复外层状态。
     */
    @Test
    public void shouldApplyAnyRepeatedAuthUserAndRestoreOuterScope() {
        initUser(SystemUser.SYSTEM.getUserUuid());
        ApiAuthContext.enter(MultipleSystemUserApi.class);
        Assert.assertTrue(AuthActionChecker.check(TestAuth.class));

        ApiAuthContext.enter(OtherSystemUserApi.class);
        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));
        ApiAuthContext.exit();
        Assert.assertTrue(AuthActionChecker.check(TestAuth.class));
        ApiAuthContext.exit();

        UserContext.get().release();
        initUser(SystemUser.ANONYMOUS.getUserUuid());
        ApiAuthContext.enter(MultipleSystemUserApi.class);
        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));
    }

    /**
     * 普通 ThreadLocal 不向接口派生线程传播，异步线程保持后台系统用户放行规则。
     */
    @Test
    public void shouldNotPropagateApiScopeToAsyncThread() throws Exception {
        initUser(SystemUser.SYSTEM.getUserUuid());
        ApiAuthContext.enter(OtherSystemUserApi.class);
        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));

        AtomicBoolean asyncResult = new AtomicBoolean(false);
        Thread thread = new Thread(() -> asyncResult.set(AuthActionChecker.check(
                SystemUser.SYSTEM.getUserUuid(), TestAuth.class)));
        thread.start();
        thread.join();

        Assert.assertTrue(asyncResult.get());
    }

    /**
     * API 执行抛出异常时也必须清理当前作用域。
     */
    @Test(expected = Exception.class)
    public void shouldRestoreScopeAfterException() throws Exception {
        initUser(SystemUser.SYSTEM.getUserUuid());
        try {
            new TestApiTemplate().invokeFailure(OtherSystemUserApi.class);
        } finally {
            Assert.assertTrue(AuthActionChecker.check(TestAuth.class));
        }
    }

    /**
     * 指定其他用户时必须查询目标用户权限，不能复用当前登录人的权限。
     */
    @Test
    public void shouldCheckTargetUserPermissions() {
        initUser("user-a");
        USER_MAP.put("user-b", createUser("user-b", false));
        USER_AUTH_MAP.put("user-a", Collections.singletonList(new UserAuthVo("user-a", TestAuth.class.getSimpleName())));
        USER_AUTH_MAP.put("user-b", Collections.emptyList());

        Assert.assertFalse(AuthActionChecker.check("user-b", TestAuth.class));

        USER_AUTH_MAP.put("user-a", Collections.emptyList());
        USER_AUTH_MAP.put("user-b", Collections.singletonList(new UserAuthVo("user-b", TestAuth.class.getSimpleName())));
        Assert.assertTrue(AuthActionChecker.check("user-b", TestAuth.class));
        Assert.assertEquals(2, AUTHENTICATION_INFO_QUERY_COUNT.get());
    }

    /**
     * 超级管理员状态只对目标用户生效。
     */
    @Test
    public void shouldCheckTargetUserSuperAdminStatus() {
        initUser("user-a", true);
        USER_MAP.put("user-b", createUser("user-b", false));
        USER_MAP.put("user-c", createUser("user-c", true));

        Assert.assertFalse(AuthActionChecker.check("user-b", TestAuth.class));
        Assert.assertTrue(AuthActionChecker.check("user-c", TestAuth.class));
        Assert.assertTrue(AuthActionChecker.check("user-a", TestAuth.class));
    }

    /**
     * 目标用户就是当前用户时信任并复用上下文，不重复查询用户状态或鉴权信息。
     */
    @Test
    public void shouldReuseCurrentUserAuthenticationInfo() {
        initUser("user-a");
        userAuthList = Collections.singletonList(new UserAuthVo("user-a", TestAuth.class.getSimpleName()));
        UserVo inactiveCurrentUser = createUser("user-a", false);
        inactiveCurrentUser.setIsActive(0);
        inactiveCurrentUser.setIsDelete(1);
        USER_MAP.put("user-a", inactiveCurrentUser);

        Assert.assertTrue(AuthActionChecker.check("user-a", TestAuth.class));
        Assert.assertTrue(AuthActionChecker.check("user-a", NoAuth.class));
        Assert.assertEquals(0, USER_QUERY_COUNT.get());
        Assert.assertEquals(0, AUTHENTICATION_INFO_QUERY_COUNT.get());
        Assert.assertEquals(1, AUTHORIZATION_QUERY_COUNT.get());
    }

    /**
     * 跨用户或无上下文校验时，普通目标用户必须存在、启用且未删除，NoAuth 除外。
     */
    @Test
    public void shouldRejectInvalidTargetUsersBeforeCheckingPermissions() {
        initUser("user-a");
        UserVo deletedSuperAdmin = createUser("deleted-admin", true);
        deletedSuperAdmin.setIsDelete(1);
        USER_MAP.put("deleted-admin", deletedSuperAdmin);
        USER_AUTH_MAP.put("deleted-admin", Collections.singletonList(
                new UserAuthVo("deleted-admin", TestAuth.class.getSimpleName())));

        UserVo inactiveUser = createUser("inactive-user", false);
        inactiveUser.setIsActive(0);
        USER_MAP.put("inactive-user", inactiveUser);
        USER_AUTH_MAP.put("inactive-user", Collections.singletonList(
                new UserAuthVo("inactive-user", TestAuth.class.getSimpleName())));

        Assert.assertFalse(AuthActionChecker.check("deleted-admin", TestAuth.class));
        Assert.assertFalse(AuthActionChecker.check("inactive-user", TestAuth.class));
        Assert.assertFalse(AuthActionChecker.check("missing-user", TestAuth.class));
        Assert.assertTrue(AuthActionChecker.check("inactive-user", NoAuth.class));
        Assert.assertTrue(AuthActionChecker.check("missing-user", NoAuth.class));
        Assert.assertEquals(3, USER_QUERY_COUNT.get());
        Assert.assertEquals(0, AUTHENTICATION_INFO_QUERY_COUNT.get());
        Assert.assertEquals(0, AUTHORIZATION_QUERY_COUNT.get());
    }

    /**
     * 没有当前用户上下文时，显式校验仍只依据目标用户自身状态和权限。
     */
    @Test
    public void shouldCheckValidTargetWithoutUserContext() {
        USER_MAP.put("normal-user", createUser("normal-user", false));
        USER_MAP.put("admin-user", createUser("admin-user", true));
        USER_AUTH_MAP.put("normal-user", Collections.singletonList(
                new UserAuthVo("normal-user", TestAuth.class.getSimpleName())));

        Assert.assertTrue(AuthActionChecker.check("normal-user", TestAuth.class));
        Assert.assertTrue(AuthActionChecker.check("admin-user", TestAuth.class));
        Assert.assertFalse(AuthActionChecker.check("missing-user", TestAuth.class));
        Assert.assertEquals(1, AUTHENTICATION_INFO_QUERY_COUNT.get());
        Assert.assertEquals(1, AUTHORIZATION_QUERY_COUNT.get());
    }

    /**
     * 权限类型数组中的空元素按无效输入处理，不进入任何用户或权限查询。
     */
    @Test
    @SuppressWarnings("unchecked")
    public void shouldRejectNullAuthClassElements() {
        initUser("user-a");

        Assert.assertFalse(AuthActionChecker.check((Class<? extends AuthBase>[]) null));
        Assert.assertFalse(AuthActionChecker.check(new Class[0]));
        Assert.assertFalse(AuthActionChecker.check(TestAuth.class, null));
        Assert.assertFalse(AuthActionChecker.check("user-a", (Class<? extends AuthBase>[]) null));
        Assert.assertFalse(AuthActionChecker.check("user-a", new Class[0]));
        Assert.assertFalse(AuthActionChecker.check("user-a", TestAuth.class, null));
        Assert.assertEquals(0, USER_QUERY_COUNT.get());
        Assert.assertEquals(0, AUTHENTICATION_INFO_QUERY_COUNT.get());
        Assert.assertEquals(0, AUTHORIZATION_QUERY_COUNT.get());
    }

    /**
     * NeatLogicThread 只复制用户上下文，不继承同步 API 的系统用户鉴权状态。
     */
    @Test
    public void shouldNotPropagateApiScopeToNeatLogicThread() throws Exception {
        initUser(SystemUser.SYSTEM.getUserUuid());
        TenantContext.init("test-tenant");
        ApiAuthContext.enter(OtherSystemUserApi.class);
        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));

        AtomicBoolean asyncResult = new AtomicBoolean(false);
        NeatLogicThread task = new NeatLogicThread("api-auth-context-test") {
            @Override
            protected void execute() {
                asyncResult.set(AuthActionChecker.check(TestAuth.class));
            }
        };
        task.setNeedAwaitAdvance(false);
        ApiAuthContext.exit();
        Thread thread = new Thread(task);
        thread.start();
        thread.join();

        Assert.assertTrue(asyncResult.get());
    }

    /**
     * 维护用户拥有任一维护权限即可通过，并安全拒绝空用户 UUID。
     */
    @Test
    public void shouldApplyMaintenanceAnyMatchAndRejectBlankUserUuid() throws Exception {
        Field enableMaintenanceField = Config.class.getDeclaredField("ENABLE_MAINTENANCE");
        Field maintenanceField = Config.class.getDeclaredField("MAINTENANCE");
        enableMaintenanceField.setAccessible(true);
        maintenanceField.setAccessible(true);
        Object originalEnableMaintenance = enableMaintenanceField.get(null);
        Object originalMaintenance = maintenanceField.get(null);
        try {
            enableMaintenanceField.set(null, true);
            maintenanceField.set(null, "maintenance-user");

            Assert.assertTrue(AuthActionChecker.check("maintenance-user", USER_MODIFY.class, TestAuth.class));
            Assert.assertFalse(AuthActionChecker.check("maintenance-user", TestAuth.class));
            String nullUserUuid = null;
            Assert.assertFalse(AuthActionChecker.check(nullUserUuid, TestAuth.class));
            Assert.assertFalse(AuthActionChecker.check("", TestAuth.class));
            Assert.assertFalse(AuthActionChecker.check("  ", TestAuth.class));
        } finally {
            enableMaintenanceField.set(null, originalEnableMaintenance);
            maintenanceField.set(null, originalMaintenance);
        }
    }

    /**
     * 指定用户校验继续保留 NoAuth 和权限包含关系的既有语义。
     */
    @Test
    @SuppressWarnings("unchecked")
    public void shouldPreserveNoAuthAndIncludedAuthSemantics() throws Exception {
        initUser("user-a");
        Assert.assertTrue(AuthActionChecker.check("user-a", NoAuth.class));

        Field authMapField = AuthFactory.class.getDeclaredField("authMap");
        authMapField.setAccessible(true);
        Map<String, AuthBase> authMap = (Map<String, AuthBase>) authMapField.get(null);
        AuthBase originalParentAuth = authMap.put(TEST_PARENT_AUTH.class.getSimpleName(), new TEST_PARENT_AUTH());
        AuthBase originalIncludedAuth = authMap.put(TEST_INCLUDED_AUTH.class.getSimpleName(), new TEST_INCLUDED_AUTH());
        try {
            userAuthList = Collections.singletonList(
                    new UserAuthVo("user-a", TEST_PARENT_AUTH.class.getSimpleName()));
            Assert.assertTrue(AuthActionChecker.check("user-a", TEST_INCLUDED_AUTH.class));
        } finally {
            restoreAuth(authMap, TEST_PARENT_AUTH.class.getSimpleName(), originalParentAuth);
            restoreAuth(authMap, TEST_INCLUDED_AUTH.class.getSimpleName(), originalIncludedAuth);
        }
    }

    /**
     * 多层、循环、未知和重复权限均应稳定完成递归判断。
     */
    @Test
    @SuppressWarnings("unchecked")
    public void shouldHandleRecursiveAuthBoundaries() throws Exception {
        initUser("user-a");
        Field authMapField = AuthFactory.class.getDeclaredField("authMap");
        authMapField.setAccessible(true);
        Map<String, AuthBase> authMap = (Map<String, AuthBase>) authMapField.get(null);
        Map<String, AuthBase> originalAuthMap = new HashMap<>();
        registerAuth(authMap, originalAuthMap, new TEST_RECURSIVE_A());
        registerAuth(authMap, originalAuthMap, new TEST_RECURSIVE_B());
        registerAuth(authMap, originalAuthMap, new TEST_RECURSIVE_C());
        try {
            userAuthList = Arrays.asList(
                    new UserAuthVo("user-a", TEST_RECURSIVE_A.class.getSimpleName()),
                    new UserAuthVo("user-a", TEST_RECURSIVE_A.class.getSimpleName()),
                    new UserAuthVo("user-a", "UNKNOWN_HISTORY_AUTH"));
            Assert.assertTrue(AuthActionChecker.check("user-a", TEST_RECURSIVE_C.class));
            Assert.assertFalse(AuthActionChecker.check("user-a", TestAuth.class));
        } finally {
            restoreAuth(authMap, TEST_RECURSIVE_A.class.getSimpleName(), originalAuthMap.get(TEST_RECURSIVE_A.class.getSimpleName()));
            restoreAuth(authMap, TEST_RECURSIVE_B.class.getSimpleName(), originalAuthMap.get(TEST_RECURSIVE_B.class.getSimpleName()));
            restoreAuth(authMap, TEST_RECURSIVE_C.class.getSimpleName(), originalAuthMap.get(TEST_RECURSIVE_C.class.getSimpleName()));
        }
    }

    /**
     * 临时注册递归测试权限，并保存可能存在的原始实例。
     */
    private void registerAuth(Map<String, AuthBase> authMap, Map<String, AuthBase> originalAuthMap, AuthBase auth) {
        originalAuthMap.put(auth.getAuthName(), authMap.put(auth.getAuthName(), auth));
    }

    /**
     * 恢复测试期间临时注册的权限类型。
     */
    private void restoreAuth(Map<String, AuthBase> authMap, String authName, AuthBase originalAuth) {
        if (originalAuth == null) {
            authMap.remove(authName);
        } else {
            authMap.put(authName, originalAuth);
        }
    }

    /**
     * 初始化指定身份的最小用户上下文，避免测试依赖 JWT 和租户配置。
     */
    private void initUser(String userUuid) {
        initUser(userUuid, false);
    }

    /**
     * 初始化指定身份及超级管理员状态的最小用户上下文。
     */
    private void initUser(String userUuid, boolean isSuperAdmin) {
        UserVo userVo = new UserVo();
        userVo.setUuid(userUuid);
        userVo.setUserId(userUuid);
        userVo.setAuthorization("test-authorization");
        userVo.setIsSuperAdmin(isSuperAdmin);
        UserContext.init(userVo, new AuthenticationInfoVo(userUuid), TimeUtil.ZONE_TIME);
        USER_MAP.put(userUuid, createUser(userUuid, isSuperAdmin));
    }

    /**
     * 创建指定超级管理员状态的测试用户。
     */
    private UserVo createUser(String userUuid, boolean isSuperAdmin) {
        UserVo userVo = new UserVo();
        userVo.setUuid(userUuid);
        userVo.setIsActive(1);
        userVo.setIsDelete(0);
        userVo.setIsSuperAdmin(isSuperAdmin);
        return userVo;
    }

    /**
     * 豁免 SYSTEM 用户的测试接口。
     */
    @AuthUser(SystemUser.SYSTEM)
    private static class SystemUserApi {
    }

    /**
     * 仅豁免 AUTOEXEC 用户的测试接口。
     */
    @AuthUser(SystemUser.AUTOEXEC)
    private static class OtherSystemUserApi {
    }

    /**
     * 同时豁免 AUTOEXEC 和 SYSTEM 用户的测试接口。
     */
    @AuthUser(SystemUser.AUTOEXEC)
    @AuthUser(SystemUser.SYSTEM)
    private static class MultipleSystemUserApi {
    }

    /**
     * 暴露公共模板的鉴权作用域包装，验证异常清理行为。
     */
    private static class TestApiTemplate extends ApiComponentTemplateBase {
        /**
         * 在未豁免接口作用域内抛出测试异常。
         */
        private void invokeFailure(Class<?> apiClass) throws Exception {
            invokeWithApiAuthContext(apiClass, () -> {
                throw new Exception("test");
            });
        }
    }

    /**
     * 测试专用权限类型，只用于构造一个未授予的权限名称。
     */
    public static class TestAuth extends AuthBase {
        @Override
        public String getAuthDisplayName() {
            return "test";
        }

        @Override
        public String getAuthIntroduction() {
            return "test";
        }

        @Override
        public String getAuthGroup() {
            return "test";
        }

        @Override
        public Integer getSort() {
            return 0;
        }
    }

    /**
     * 测试权限包含关系的父权限。
     */
    public static class TEST_PARENT_AUTH extends AuthBase {
        @Override
        public String getAuthDisplayName() {
            return "test.parent";
        }

        @Override
        public String getAuthIntroduction() {
            return "test.parent";
        }

        @Override
        public String getAuthGroup() {
            return "test";
        }

        @Override
        public Integer getSort() {
            return 0;
        }

        @Override
        public List<Class<? extends AuthBase>> getIncludeAuths() {
            return Collections.singletonList(TEST_INCLUDED_AUTH.class);
        }
    }

    /**
     * 测试权限包含关系的子权限。
     */
    public static class TEST_INCLUDED_AUTH extends TestAuth {
    }

    /**
     * 多层递归测试的根权限。
     */
    public static class TEST_RECURSIVE_A extends TestAuth {
        @Override
        public List<Class<? extends AuthBase>> getIncludeAuths() {
            return Collections.singletonList(TEST_RECURSIVE_B.class);
        }
    }

    /**
     * 多层递归测试的中间权限，并回指根权限构造循环。
     */
    public static class TEST_RECURSIVE_B extends TestAuth {
        @Override
        public List<Class<? extends AuthBase>> getIncludeAuths() {
            return Arrays.asList(TEST_RECURSIVE_C.class, TEST_RECURSIVE_A.class);
        }
    }

    /**
     * 多层递归测试的目标权限。
     */
    public static class TEST_RECURSIVE_C extends TestAuth {
    }
}
