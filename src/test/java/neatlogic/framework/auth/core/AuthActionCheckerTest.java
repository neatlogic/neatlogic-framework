package neatlogic.framework.auth.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.init.MaintenanceMode;
import neatlogic.framework.auth.label.NoAuth;
import neatlogic.framework.auth.label.USER_MODIFY;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.systemuser.ISystemUser;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.config.FrameworkTenantConfig;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.UserAuthVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.service.AuthenticationInfoService;
import org.junit.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 验证系统用户统一授权及指定用户权限校验边界。
 */
public class AuthActionCheckerTest {
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

    /** 反射注入测试权限后同步默认授权索引，避免测试依赖生产注册顺序。 */
    private static void rebuildDefaultAuthIndex() throws Exception {
        Method method = AuthFactory.class.getDeclaredMethod("initializeDefaultAuthMap");
        method.setAccessible(true);
        method.invoke(null);
    }

    /**
     * 安装不返回任何权限的 UserMapper，隔离数据库并使未授权路径稳定返回无权限。
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

        if (UserContext.get() != null) {
            UserContext.get().release();
        }
        TenantContext.get().release();
    }

    /**
     * 所有系统用户校验入口都必须进入实际权限判断。
     */
    @Test
    public void shouldRequireActualAuthForSystemUser() {
        initUser(SystemUser.SYSTEM.getUserUuid());

        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));
        Assert.assertFalse(AuthActionChecker.check(SystemUser.SYSTEM.getUserUuid(), TestAuth.class));
    }

    /**
     * 系统用户拥有页面授权时正常通过。
     */
    @Test
    public void shouldAllowSystemUserWithGrantedAuth() {
        initUser(SystemUser.SYSTEM.getUserUuid());
        userAuthList = Collections.singletonList(new UserAuthVo(SystemUser.SYSTEM.getUserUuid(), TestAuth.class.getSimpleName()));

        Assert.assertTrue(AuthActionChecker.check(TestAuth.class));
        Assert.assertTrue(AuthActionChecker.check(SystemUser.SYSTEM.getUserUuid(), TestAuth.class));
    }

    /** 系统身份不能借用当前上下文的超级管理员标记放行。 */
    @Test
    public void shouldIgnoreSuperAdminFlagForSystemUser() {
        initUser(SystemUser.SYSTEM.getUserUuid(), true);
        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));
        Assert.assertEquals(0, USER_QUERY_COUNT.get());
        Assert.assertEquals(0, AUTHENTICATION_INFO_QUERY_COUNT.get());
    }

    /** 默认授权与页面授权取并集，包含关系复用原逻辑，并受租户模块启用状态约束。 */
    @Test
    @SuppressWarnings("unchecked")
    public void shouldMergeCodeAndPageAuthWithinActiveModules() throws Exception {
        Field authMapField = AuthFactory.class.getDeclaredField("authMap");
        authMapField.setAccessible(true);
        Map<String, AuthBase> authMap = (Map<String, AuthBase>) authMapField.get(null);
        Map<String, AuthBase> originalAuthMap = new HashMap<>(authMap);
        Field cacheField = TenantContext.class.getDeclaredField("tenantModuleGroupListMap");
        cacheField.setAccessible(true);
        Map<String, List<String>> cache = (Map<String, List<String>>) cacheField.get(null);
        String tenantUuid = "code-auth-test-tenant";
        List<String> originalGroups = cache.put(tenantUuid, new ArrayList<>(Collections.singletonList("test")));
        ModuleVo module = new ModuleVo();
        module.setId("code-auth-test-module");
        module.setGroup("test");
        ModuleUtil.addModule(module);
        TenantContext.init(tenantUuid);
        authMap.put(TEST_CODE_AUTH.class.getSimpleName(), new TEST_CODE_AUTH());
        rebuildDefaultAuthIndex();
        authMap.put(TEST_INCLUDED_AUTH.class.getSimpleName(), new TEST_INCLUDED_AUTH());
        rebuildDefaultAuthIndex();
        try {
            initUser(SystemUser.SYSTEM.getUserUuid());
            USER_AUTH_MAP.put(SystemUser.SYSTEM.getUserUuid(), Collections.singletonList(
                    new UserAuthVo(SystemUser.SYSTEM.getUserUuid(), TestAuth.class.getSimpleName())));
            Assert.assertTrue(AuthActionChecker.check(TEST_CODE_AUTH.class));
            Assert.assertTrue(AuthActionChecker.check(TEST_INCLUDED_AUTH.class));
            Assert.assertEquals("代码直接及包含权限命中不查询数据库", 0, AUTHORIZATION_QUERY_COUNT.get());
            Assert.assertTrue(AuthActionChecker.check(TestAuth.class));
            Assert.assertTrue(AuthFactory.getDefaultAuthListBySystemUser("normal-user").isEmpty());

            // 撤销页面记录不会撤销代码默认权限，也不会保留已撤销的页面权限。
            USER_AUTH_MAP.put(SystemUser.SYSTEM.getUserUuid(), Collections.emptyList());
            Assert.assertTrue(AuthActionChecker.check(TEST_INCLUDED_AUTH.class));
            Assert.assertFalse(AuthActionChecker.check(TestAuth.class));
            List<UserAuthVo> expanded = new ArrayList<>(Collections.singletonList(
                    new UserAuthVo(SystemUser.SYSTEM.getUserUuid(), new TEST_CODE_AUTH())));
            UserContext.get().release();
            AuthActionChecker.getAuthList(expanded);
            Assert.assertEquals(2, expanded.size());
            Assert.assertTrue(expanded.stream().allMatch(auth -> SystemUser.SYSTEM.getUserUuid().equals(auth.getUserUuid())));
            Assert.assertTrue(AuthActionChecker.check(SystemUser.SYSTEM.getUserUuid(), TEST_INCLUDED_AUTH.class));

            cache.put(tenantUuid, new ArrayList<>());
            Assert.assertTrue(AuthFactory.getDefaultAuthListBySystemUser(SystemUser.SYSTEM.getUserUuid()).isEmpty());
            Assert.assertFalse(AuthActionChecker.check(SystemUser.SYSTEM.getUserUuid(), TEST_INCLUDED_AUTH.class));
            Assert.assertEquals(0, USER_QUERY_COUNT.get());
        } finally {
            authMap.clear();
            authMap.putAll(originalAuthMap);
            rebuildDefaultAuthIndex();
            ModuleUtil.removeModule(module);
            if (originalGroups == null) {
                cache.remove(tenantUuid);
            } else {
                cache.put(tenantUuid, originalGroups);
            }
        }
    }

    /**
     * 系统身份不会向普通用户授予权限。
     */
    @Test
    public void shouldNotGrantSystemPermissionsToNormalUser() {
        initUser("normal-user");

        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));
    }

    /**
     * 普通异步线程必须按指定系统用户的实际权限校验。
     */
    @Test
    public void shouldRequireAuthInAsyncThread() throws Exception {
        initUser(SystemUser.SYSTEM.getUserUuid());

        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));

        AtomicBoolean asyncResult = new AtomicBoolean(false);
        Thread thread = new Thread(() -> asyncResult.set(AuthActionChecker.check(
                SystemUser.SYSTEM.getUserUuid(), TestAuth.class)));
        thread.start();
        thread.join();

        Assert.assertFalse(asyncResult.get());
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
     * NeatLogicThread 复制用户上下文后仍执行实际权限校验。
     */
    @Test
    public void shouldRequireAuthInNeatLogicThread() throws Exception {
        initUser(SystemUser.SYSTEM.getUserUuid());
        TenantContext.init("test-tenant");

        Assert.assertFalse(AuthActionChecker.check(TestAuth.class));

        AtomicBoolean asyncResult = new AtomicBoolean(false);
        NeatLogicThread task = new NeatLogicThread("system-auth-check-test") {
            @Override
            protected void execute() {
                asyncResult.set(AuthActionChecker.check(TestAuth.class));
            }
        };
        task.setNeedAwaitAdvance(false);

        Thread thread = new Thread(task);
        thread.start();
        thread.join();

        Assert.assertFalse(asyncResult.get());
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

    /** 商业权限未注册或模块已失效时，历史直接授权不能放行任何普通目标用户。 */
    @Test
    public void shouldRejectHistoricalCommercialAuthForCurrentAndTargetUsers() throws Exception {
        try (CommercialAuthFixture fixture = new CommercialAuthFixture()) {
            initUser("commercial-user");
            userAuthList = Collections.singletonList(new UserAuthVo("commercial-user", TEST_COMMERCIAL_AUTH.class.getSimpleName()));
            USER_MAP.put("other-user", createUser("other-user", false));
            USER_AUTH_MAP.put("other-user", userAuthList);
            Assert.assertTrue(AuthFactory.isAuthLicenseValid(TEST_COMMERCIAL_AUTH.class));
            Assert.assertTrue(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class));
            Assert.assertTrue(AuthActionChecker.check("other-user", TEST_COMMERCIAL_AUTH.class));
            fixture.invalidate();
            Assert.assertFalse(AuthFactory.isAuthLicenseValid(TEST_COMMERCIAL_AUTH.class));
            Assert.assertFalse(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class));
            Assert.assertFalse(AuthActionChecker.check("other-user", TEST_COMMERCIAL_AUTH.class));
            fixture.validate();
            fixture.authMap.remove(TEST_COMMERCIAL_AUTH.class.getSimpleName());
            Assert.assertFalse(AuthFactory.isAuthLicenseValid(TEST_COMMERCIAL_AUTH.class));
            Assert.assertFalse(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class));
            Assert.assertFalse(AuthActionChecker.check("other-user", TEST_COMMERCIAL_AUTH.class));
            Assert.assertEquals("授权记录不删除", 1, userAuthList.size());
        }
    }

    /** 商业目标检查先于当前及指定超级管理员的身份放行。 */
    @Test
    public void shouldRejectUnlicensedCommercialAuthForSuperAdmins() throws Exception {
        try (CommercialAuthFixture fixture = new CommercialAuthFixture()) {
            initUser("commercial-admin", true);
            USER_MAP.put("target-admin", createUser("target-admin", true));
            Assert.assertTrue(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class));
            Assert.assertTrue(AuthActionChecker.check("target-admin", TEST_COMMERCIAL_AUTH.class));
            fixture.invalidate();
            Assert.assertFalse(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class));
            Assert.assertFalse(AuthActionChecker.check("target-admin", TEST_COMMERCIAL_AUTH.class));
            Assert.assertTrue(AuthActionChecker.check(TestAuth.class));
        }
    }

    /** 系统用户的出厂与历史页面授权不能覆盖商业模块失效状态。 */
    @Test
    public void shouldRejectCommercialCodeAndPageAuthForSystemUsers() throws Exception {
        try (CommercialAuthFixture fixture = new CommercialAuthFixture()) {
            initUser(SystemUser.SYSTEM.getUserUuid());
            userAuthList = Collections.singletonList(new UserAuthVo(SystemUser.SYSTEM.getUserUuid(), TEST_COMMERCIAL_AUTH.class.getSimpleName()));
            Assert.assertTrue(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class));
            Assert.assertEquals("先命中出厂授权", 0, AUTHORIZATION_QUERY_COUNT.get());
            fixture.invalidate();
            Assert.assertFalse(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class));
            Assert.assertFalse(AuthActionChecker.check(SystemUser.SYSTEM.getUserUuid(), TEST_COMMERCIAL_AUTH.class));
            Assert.assertEquals("失效目标在查询历史页面授权前拒绝", 0, AUTHORIZATION_QUERY_COUNT.get());
            fixture.validate();
            fixture.authMap.remove(TEST_COMMERCIAL_AUTH.class.getSimpleName());
            rebuildDefaultAuthIndex();
            Assert.assertFalse(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class));
        }
    }

    /** 即使商业权限被列入维护集合也不能绕过授权，其他维护权限保留任选其一行为。 */
    @Test
    public void shouldFilterCommercialAuthBeforeMaintenanceBypass() throws Exception {
        Field enabled = Config.class.getDeclaredField("ENABLE_MAINTENANCE");
        Field maintenance = Config.class.getDeclaredField("MAINTENANCE");
        enabled.setAccessible(true);
        maintenance.setAccessible(true);
        Object oldEnabled = enabled.get(null);
        Object oldMaintenance = maintenance.get(null);
        boolean added = MaintenanceMode.maintenanceAuthSet.add(TEST_COMMERCIAL_AUTH.class.getSimpleName());
        try (CommercialAuthFixture fixture = new CommercialAuthFixture()) {
            enabled.set(null, true);
            maintenance.set(null, "commercial-maintenance");
            Assert.assertTrue(AuthActionChecker.check("commercial-maintenance", TEST_COMMERCIAL_AUTH.class));
            fixture.invalidate();
            Assert.assertFalse(AuthActionChecker.check("commercial-maintenance", TEST_COMMERCIAL_AUTH.class));
            Assert.assertTrue(AuthActionChecker.check("commercial-maintenance", TEST_COMMERCIAL_AUTH.class, USER_MODIFY.class));
        } finally {
            enabled.set(null, oldEnabled);
            maintenance.set(null, oldMaintenance);
            if (added) MaintenanceMode.maintenanceAuthSet.remove(TEST_COMMERCIAL_AUTH.class.getSimpleName());
        }
    }

    /** 失效商业目标被排除，不改变有效商业权限、非商业权限及 NoAuth 的任选其一语义。 */
    @Test
    public void shouldPreserveAlternativePermissionsAndNoAuth() throws Exception {
        try (CommercialAuthFixture fixture = new CommercialAuthFixture()) {
            initUser("commercial-user");
            userAuthList = Arrays.asList(new UserAuthVo("commercial-user", TEST_COMMERCIAL_AUTH.class.getSimpleName()),
                    new UserAuthVo("commercial-user", TestAuth.class.getSimpleName()));
            fixture.invalidate();
            Assert.assertFalse(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class));
            Assert.assertTrue(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class, TestAuth.class));
            Assert.assertTrue(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class, NoAuth.class));
            Assert.assertFalse(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class, NoAuth.class, null));
            Assert.assertTrue(AuthFactory.isAuthLicenseValid(TestAuth.class));
            Assert.assertFalse(AuthFactory.isAuthLicenseValid(null));
            fixture.validate();
            Assert.assertTrue(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class));
        }
    }

    /** 失效商业父子节点不能传递或展开权限，有效混合循环仍可终止并展开。 */
    @Test
    public void shouldStopInvalidCommercialBranchesAndPreserveCycles() throws Exception {
        try (CommercialAuthFixture fixture = new CommercialAuthFixture()) {
            initUser("commercial-user");
            userAuthList = Collections.singletonList(new UserAuthVo("commercial-user", TEST_COMMERCIAL_WRAPPER.class.getSimpleName()));
            Assert.assertTrue(AuthActionChecker.check(TEST_INCLUDED_AUTH.class));
            Assert.assertFalse(AuthActionChecker.check(TestAuth.class));
            List<UserAuthVo> expanded = new ArrayList<>(userAuthList);
            AuthActionChecker.getAuthList(expanded);
            Assert.assertEquals(3, expanded.size());
            fixture.invalidate();
            Assert.assertFalse(AuthActionChecker.check(TEST_COMMERCIAL_AUTH.class));
            Assert.assertFalse(AuthActionChecker.check(TEST_INCLUDED_AUTH.class));
            expanded = new ArrayList<>(userAuthList);
            AuthActionChecker.getAuthList(expanded);
            Assert.assertEquals("普通父权限不展开失效商业子分支", 1, expanded.size());
            userAuthList = Collections.singletonList(new UserAuthVo("commercial-user", TEST_COMMERCIAL_AUTH.class.getSimpleName()));
            Assert.assertFalse(AuthActionChecker.check(TEST_INCLUDED_AUTH.class));
            expanded = new ArrayList<>(userAuthList);
            AuthActionChecker.getAuthList(expanded);
            Assert.assertEquals("历史商业父记录仍保留，但不授予其包含权限", 1, expanded.size());
            fixture.validate();
            fixture.authMap.remove(TEST_COMMERCIAL_AUTH.class.getSimpleName());
            Assert.assertFalse(AuthActionChecker.check(TEST_INCLUDED_AUTH.class));
        }
    }

    /** 临时注册商业测试权限，并完整还原注册表、模块状态和系统默认权限索引。 */
    private static class CommercialAuthFixture implements AutoCloseable {
        private static final String MODULE = "commercial-auth-test";
        private final Map<String, AuthBase> authMap;
        private final Map<String, AuthBase> originalAuthMap;
        private final Set<String> invalidModules;
        private final Set<String> originalInvalidModules;
        private final Map<String, List<String>> tenantGroups;
        private final String tenant = "commercial-auth-test-tenant";
        private final List<String> originalGroups;
        private final Object oldDefaultAuthMap;
        private final Field defaultAuthField;
        private final ModuleVo module = new ModuleVo();

        /** 所有替身均只在测试内装配，不增加生产注入入口。 */
        @SuppressWarnings("unchecked")
        private CommercialAuthFixture() throws Exception {
            Field map = AuthFactory.class.getDeclaredField("authMap");
            map.setAccessible(true);
            authMap = (Map<String, AuthBase>) map.get(null);
            originalAuthMap = new HashMap<>(authMap);
            Field invalid = ModuleUtil.class.getDeclaredField("invalidatedModuleSet");
            invalid.setAccessible(true);
            invalidModules = (Set<String>) invalid.get(null);
            originalInvalidModules = new HashSet<>(invalidModules);
            invalidModules.remove(MODULE);
            defaultAuthField = AuthFactory.class.getDeclaredField("defaultAuthMap");
            defaultAuthField.setAccessible(true);
            oldDefaultAuthMap = defaultAuthField.get(null);
            Field groups = TenantContext.class.getDeclaredField("tenantModuleGroupListMap");
            groups.setAccessible(true);
            tenantGroups = (Map<String, List<String>>) groups.get(null);
            originalGroups = tenantGroups.put(tenant, new ArrayList<>(Collections.singletonList(MODULE)));
            module.setId(MODULE);
            module.setGroup(MODULE);
            ModuleUtil.addModule(module);
            TenantContext.init(tenant);
            authMap.put(TEST_COMMERCIAL_AUTH.class.getSimpleName(), new TEST_COMMERCIAL_AUTH());
            authMap.put(TEST_COMMERCIAL_WRAPPER.class.getSimpleName(), new TEST_COMMERCIAL_WRAPPER());
            authMap.put(TEST_INCLUDED_AUTH.class.getSimpleName(), new TEST_INCLUDED_AUTH());
            rebuildDefaultAuthIndex();
        }

        /** 模拟已注册权限的所属模块失效。 */
        private void invalidate() { ModuleUtil.addInvalidatedModule(MODULE); }
        /** 模拟有效授权恢复，注册表与历史授权均保留。 */
        private void validate() { invalidModules.remove(MODULE); }

        /** 无论断言是否失败均恢复静态状态，避免影响后续测试。 */
        @Override public void close() throws Exception {
            authMap.clear();
            authMap.putAll(originalAuthMap);
            invalidModules.clear();
            invalidModules.addAll(originalInvalidModules);
            defaultAuthField.set(null, oldDefaultAuthMap);
            ModuleUtil.removeModule(module);
            if (originalGroups == null) {
                tenantGroups.remove(tenant);
            } else {
                tenantGroups.put(tenant, originalGroups);
            }
        }
    }

    /** 同时拥有普通子权限和反向包含关系的商业权限，用于验证失效中转与循环。 */
    public static class TEST_COMMERCIAL_AUTH extends AuthCSBase {
        /** 测试权限的显示名。 */
        @Override public String getAuthDisplayName() { return "test.commercial"; }
        /** 测试权限的说明。 */
        @Override public String getAuthIntroduction() { return "test.commercial"; }
        /** 隔离生产模块权限。 */
        @Override public String getAuthGroup() { return CommercialAuthFixture.MODULE; }
        /** 测试模块显式声明，避免由测试包路径推断生产归属。 */
        @Override public String getAuthModule() { return CommercialAuthFixture.MODULE; }
        /** 测试排序值。 */
        @Override public Integer getSort() { return 0; }
        /** 同时包含普通权限与回指父节点，验证混合循环。 */
        @Override public List<Class<? extends AuthBase>> getIncludeAuths() {
            return Arrays.asList(TEST_INCLUDED_AUTH.class, TEST_COMMERCIAL_WRAPPER.class);
        }
        /** 系统身份的代码默认授权同样受商业有效性约束。 */
        @Override public List<ISystemUser> getDefaultSystemUserList() { return Collections.singletonList(SystemUser.SYSTEM); }
    }

    /** 普通权限包含商业分支，不应绕过商业中转节点的授权。 */
    public static class TEST_COMMERCIAL_WRAPPER extends TestAuth {
        /** 将商业节点作为唯一子权限。 */
        @Override public List<Class<? extends AuthBase>> getIncludeAuths() { return Collections.singletonList(TEST_COMMERCIAL_AUTH.class); }
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
        UserContext.init(userVo, new AuthenticationInfoVo(userUuid), FrameworkTenantConfig.TENANT_DEFAULT_TIMEZONE.getValue());
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

    /** 仅在测试注册表中声明出厂授权，不向生产权限注册表增加默认权限。 */
    public static class TEST_CODE_AUTH extends TEST_PARENT_AUTH {
        /** 测试代码声明的系统身份与权限包含关系。 */
        @Override
        public List<ISystemUser> getDefaultSystemUserList() {
            return Collections.singletonList(SystemUser.SYSTEM);
        }
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
