package neatlogic.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.auth.core.AuthFactory;
import neatlogic.framework.common.constvalue.systemuser.ISystemUser;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.common.constvalue.systemuser.SystemUserFactory;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.*;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.exception.user.UserNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

/** 使用内存 Mapper 验证完整查询链，不依赖或改写本地数据库。 */
public class UserServiceAuthTest {
    private final List<RoleAuthVo> directAuths = new ArrayList<>();
    private final List<RoleAuthVo> teamAuths = new ArrayList<>();
    private final List<UserAuthVo> userAuths = new ArrayList<>();
    private final List<TeamVo> userTeams = new ArrayList<>();
    private final List<TeamVo> detailedTeams = new ArrayList<>();
    private final List<RoleVo> directTeamRoles = new ArrayList<>();
    private final List<RoleVo> namedRoles = new ArrayList<>();
    private final Map<String, List<RoleVo>> ancestorRoles = new HashMap<>();
    private final Map<Integer, List<TeamVo>> ancestors = new HashMap<>();
    private int pathQueryCount;
    private int roleNameQueryCount;
    private final Map<String, Integer> queryCounts = new HashMap<>();
    private final Map<String, Object[]> queryArgs = new HashMap<>();
    private boolean returnNullAuths;
    // 普通用例提供存在的用户；异常及超级管理员用例按需替换该资料。
    private UserVo user = new UserVo();
    private UserServiceImpl service;
    private Map<String, AuthBase> registry;
    private Map<String, AuthBase> registryBackup;

    /** 仅允许预期读取方法，发现额外写入或逐权限查询时使测试失败。 */
    @Before
    public void setup() throws Exception {
        service = new UserServiceImpl();
        inject("userMapper", Proxy.newProxyInstance(UserMapper.class.getClassLoader(), new Class<?>[]{UserMapper.class},
                (proxy, method, args) -> {
                    // 记录真实批量查询链，白名单禁止写入和未预期的额外读取。
                    queryCounts.merge(method.getName(), 1, Integer::sum);
                    queryArgs.put(method.getName(), args);
                    switch (method.getName()) {
                        case "getUserByUuid": return user;
                        case "searchUserAuthByUserUuid": return returnNullAuths ? null : userAuths;
                        case "searchUserRoleAuthByUserUuid": return returnNullAuths ? null : directAuths;
                        default: throw new AssertionError(method.getName());
                    }
                }));
        inject("teamMapper", Proxy.newProxyInstance(TeamMapper.class.getClassLoader(), new Class<?>[]{TeamMapper.class},
                (proxy, method, args) -> {
                    // 记录真实批量查询链，白名单禁止写入和未预期的额外读取。
                    queryCounts.merge(method.getName(), 1, Integer::sum);
                    queryArgs.put(method.getName(), args);
                    switch (method.getName()) {
                        case "getTeamListByUserUuid": return userTeams;
                        case "getTeamByUuidList": return detailedTeams;
                        case "getAncestorsAndSelfByLftRht":
                            pathQueryCount++;
                            return ancestors.getOrDefault(args[0], Collections.emptyList());
                        default: throw new AssertionError(method.getName());
                    }
                }));
        inject("roleMapper", Proxy.newProxyInstance(RoleMapper.class.getClassLoader(), new Class<?>[]{RoleMapper.class},
                (proxy, method, args) -> {
                    // 记录真实批量查询链，白名单禁止写入和未预期的额外读取。
                    queryCounts.merge(method.getName(), 1, Integer::sum);
                    queryArgs.put(method.getName(), args);
                    switch (method.getName()) {
                        case "getParentTeamRoleListWithCheckedChildrenByTeam":
                            return ancestorRoles.getOrDefault(((TeamVo) args[0]).getUuid(), Collections.emptyList());
                        case "getRoleListWithTeamByTeamUuidList": return directTeamRoles;
                        case "searchRoleAuthByRoleUuidList": return returnNullAuths ? null : teamAuths;
                        case "getRoleByUuidList":
                            roleNameQueryCount++;
                            return namedRoles;
                        default: throw new AssertionError(method.getName());
                    }
                }));
    }

    /** 用户不存在时在读取授权前失败，避免把未知身份当作空权限用户。 */
    @Test
    public void shouldRejectMissingUserBeforeAuthorizationQueries() {
        user = null;
        try {
            service.getUserAuthListByUserUuid("missing-user");
            Assert.fail("未知用户应抛出异常");
        } catch (UserNotFoundException expected) {
            Assert.assertEquals(Collections.singletonMap("getUserByUuid", 1), queryCounts);
            Assert.assertEquals("missing-user", queryArgs.get("getUserByUuid")[0]);
        }
    }

    /** 内置用户无用户表记录时仍按其 UUID 读取已保存权限及包含权限。 */
    @Test
    public void shouldReadBuiltInUserAuthWithoutUserTableRecord() throws Exception {
        installGraph();
        user = null;
        userAuths.add(userAuth("process", "INC_ROOT"));
        for (SystemUser systemUser : SystemUser.values()) {
            List<UserAuthVo> result = service.getUserAuthListByUserUuid(systemUser.getUserUuid());
            Assert.assertNotNull(findAuth(result, "process", "INC_ROOT"));
            Assert.assertTrue(findAuth(result, "process", "INC_ROOT").getSourcePathList().isEmpty());
            Assert.assertFalse(findAuth(result, "cmdb", "INC_TARGET").getSourcePathList().isEmpty());
            for (UserAuthVo auth : result) {
                Assert.assertEquals(systemUser.getUserUuid(), auth.getUserUuid());
            }
            Assert.assertEquals(systemUser.getUserUuid(), queryArgs.get("searchUserAuthByUserUuid")[0]);
        }
    }

    /** 扩展内置身份只接受注册 UUID，查询不应构造用户对象、令牌或认证信息。 */
    @Test
    @SuppressWarnings("unchecked")
    public void shouldAcceptRegisteredUuidWithoutGeneratingCredentialsAndRejectAlias() throws Exception {
        Field field = SystemUserFactory.class.getDeclaredField("systemUserMap");
        field.setAccessible(true);
        Map<String, ISystemUser> systemUsers = (Map<String, ISystemUser>) field.get(null);
        Map<String, ISystemUser> backup = new HashMap<>(systemUsers);
        ISystemUser extension = (ISystemUser) Proxy.newProxyInstance(ISystemUser.class.getClassLoader(),
                new Class<?>[]{ISystemUser.class}, (proxy, method, args) -> {
                    if ("getUserId".equals(method.getName())) {
                        return "extension-id";
                    }
                    if ("getUserUuid".equals(method.getName())) {
                        return "extension-uuid";
                    }
                    throw new AssertionError("授权查询不应调用：" + method.getName());
                });
        try {
            systemUsers.put("extension-id", extension);
            user = null;
            Assert.assertTrue(service.getUserAuthListByUserUuid("extension-uuid").isEmpty());
            Assert.assertEquals("extension-uuid", queryArgs.get("searchUserAuthByUserUuid")[0]);
            queryCounts.clear();
            try {
                service.getUserAuthListByUserUuid("extension-id");
                Assert.fail("内置用户 ID 不应作为 UUID 查询授权");
            } catch (UserNotFoundException expected) {
                Assert.assertEquals(Collections.singletonMap("getUserByUuid", 1), queryCounts);
            }
        } finally {
            systemUsers.clear();
            systemUsers.putAll(backup);
        }
    }

    /** 超级管理员只返回激活模块权限，各权限来源独立，且不读取普通授权或包含定义。 */
    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnIndependentSuperAdminSourcesForActiveModules() throws Exception {
        installGraph();
        registry.clear();
        SUPER_SOURCE_A root = new SUPER_SOURCE_A();
        registry.put(root.getAuthName(), root);
        registry.put("SUPER_SOURCE_B", new SUPER_SOURCE_B());
        registry.put("SUPER_SOURCE_DISABLED", new SUPER_SOURCE_DISABLED());
        user.setIsSuperAdmin(true);
        String tenantUuid = "super-admin-source-test";
        Field cacheField = TenantContext.class.getDeclaredField("tenantModuleGroupListMap");
        cacheField.setAccessible(true);
        Map<String, List<String>> cache = (Map<String, List<String>>) cacheField.get(null);
        List<String> previousGroups = cache.put(tenantUuid, new ArrayList<>(Collections.singletonList("super-admin-source-active")));
        TenantContext previousContext = TenantContext.get();
        ModuleVo module = new ModuleVo();
        module.setId("super-admin-source-active");
        module.setGroup("super-admin-source-active");
        ModuleUtil.addModule(module);
        TenantContext.init(tenantUuid);
        try {
            List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
            Assert.assertEquals(2, result.size());
            Assert.assertEquals(new HashSet<>(Arrays.asList("SUPER_SOURCE_A", "SUPER_SOURCE_B")),
                    result.stream().map(UserAuthVo::getAuth).collect(java.util.stream.Collectors.toSet()));
            for (UserAuthVo auth : result) {
                Assert.assertEquals("u", auth.getUserUuid());
                Assert.assertEquals("super-admin-source-active", auth.getAuthGroup());
                Assert.assertNull(auth.getSourcePath());
                Assert.assertEquals(1, auth.getSourcePathList().size());
                SourcePathVo source = auth.getSourcePathList().get(0);
                Assert.assertEquals(Boolean.TRUE, source.getIsSuperAdmin());
                Assert.assertEquals(Collections.emptyList(), source.getTeamPathNameList());
                Assert.assertNull(source.getRoleUuid());
                Assert.assertNull(source.getRoleName());
                Assert.assertNull(source.getTeamUuid());
                Assert.assertNull(source.getTeamLft());
                Assert.assertNull(source.getIncludeAuthPathList());
            }
            SourcePathVo first = result.get(0).getSourcePathList().get(0);
            SourcePathVo second = result.get(1).getSourcePathList().get(0);
            Assert.assertNotSame(first, second);
            Assert.assertNotSame(first.getTeamPathNameList(), second.getTeamPathNameList());
            first.setIsSuperAdmin(false);
            first.getTeamPathNameList().add("仅修改当前输出");
            Assert.assertEquals(Boolean.TRUE, second.getIsSuperAdmin());
            Assert.assertTrue(second.getTeamPathNameList().isEmpty());
            Assert.assertEquals(0, root.includeReadCount);
            Assert.assertEquals(Collections.singletonMap("getUserByUuid", 1), queryCounts);
        } finally {
            ModuleUtil.removeModule(module);
            if (previousGroups == null) {
                cache.remove(tenantUuid);
            } else {
                cache.put(tenantUuid, previousGroups);
            }
            TenantContext.get().release();
            TenantContext.init(previousContext);
        }
    }

    /** 来源复制必须保留新增身份标识，并继续隔离可变分组路径和包含链。 */
    @Test
    public void shouldCopySuperAdminIdentityWithoutSharingMutablePaths() throws Exception {
        SourcePathVo original = new SourcePathVo();
        original.setIsSuperAdmin(true);
        original.setTeamPathNameList(new ArrayList<>());
        original.setIncludeAuthPathList(new ArrayList<>(Collections.singletonList(
                new JSONObject().fluentPut("authGroup", "process").fluentPut("auth", "ROOT"))));
        java.lang.reflect.Method copy = UserServiceImpl.class.getDeclaredMethod("copySourcePath", SourcePathVo.class);
        copy.setAccessible(true);
        SourcePathVo result = (SourcePathVo) copy.invoke(service, original);
        Assert.assertEquals(Boolean.TRUE, result.getIsSuperAdmin());
        result.setIsSuperAdmin(false);
        result.getTeamPathNameList().add("修改输出");
        result.getIncludeAuthPathList().get(0).put("auth", "CHANGED");
        Assert.assertEquals(Boolean.TRUE, original.getIsSuperAdmin());
        Assert.assertTrue(original.getTeamPathNameList().isEmpty());
        Assert.assertEquals("ROOT", original.getIncludeAuthPathList().get(0).getString("auth"));
    }

    /** 通过公开入口验证角色、分组直接绑定和祖先继承来源，原始重复记录仍保留。 */
    @Test
    public void shouldAggregateRoleAndInheritedTeamRecordsThroughPublicEntry() throws Exception {
        installGraph();
        RoleAuthVo direct = auth("r", "process", "INC_ROOT");
        directAuths.addAll(Arrays.asList(direct, direct));
        TeamVo leaf = team("leaf", "叶分组", "银行/叶分组", 2);
        userTeams.add(leaf);
        detailedTeams.add(leaf);
        namedRoles.addAll(Arrays.asList(role("r", "直接角色", null), role("ancestor", "祖先角色", null), role("own", "分组角色", null)));
        ancestorRoles.put("leaf", Collections.singletonList(role("ancestor", "祖先角色", null)));
        directTeamRoles.add(role("own", "分组角色", Collections.singletonList(leaf)));
        teamAuths.addAll(Arrays.asList(auth("ancestor", "process", "INC_ROOT"), auth("own", "process", "INC_SECOND_ROOT")));
        teamAuths.add(teamAuths.get(0));
        List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
        Assert.assertEquals(Arrays.asList("null/r", "null/r", "leaf/ancestor", "leaf/ancestor"),
                sourceIdentities(findAuth(result, "process", "INC_ROOT")));
        Assert.assertEquals(Collections.singletonList("leaf/own"), sourceIdentities(findAuth(result, "process", "INC_SECOND_ROOT")));
        Assert.assertEquals(5, findAuth(result, "cmdb", "INC_TARGET").getSourcePathList().size());
        assertTrustedRoleAndTeamSources(result);
        assertAuthorizationQueriesOnce();
        Assert.assertEquals(Arrays.asList("ancestor", "own"), queryArgs.get("searchRoleAuthByRoleUuidList")[0]);
        Assert.assertEquals("u", queryArgs.get("searchUserRoleAuthByUserUuid")[0]);
        Assert.assertEquals(1, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
        Assert.assertEquals(2, directAuths.size());
        Assert.assertEquals(3, teamAuths.size());
    }

    /** 角色查询保留原始粒度，最终按权限聚合全部来源，单纯直接授权正常返回空来源。 */
    @Test
    public void shouldPreserveRawGranularityAndAggregateFinalSources() {
        namedRoles.addAll(Arrays.asList(role("z", "Z角色", null), role("a", "A角色", null), role("r", "分组角色", null)));
        directAuths.addAll(Arrays.asList(auth("z", "process", "BASE"), auth("a", "process", "BASE"), auth("z", "process", "BASE")));
        TeamVo first = team("first", "第一组", "银行/第一组", 2);
        TeamVo second = team("second", "第二组", "银行/第二组", 4);
        userTeams.addAll(Arrays.asList(first, second));
        detailedTeams.addAll(userTeams);
        directTeamRoles.add(role("r", "分组角色", userTeams));
        teamAuths.add(auth("r", "process", "BASE"));
        // 单参数入口自行查询三类授权，检查每类查询只执行一次。
        userAuths.add(userAuth("process", "DIRECT_ONLY"));
        String before = mapperSnapshot();
        queryCounts.clear();
        List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
        assertAuthorizationQueriesOnce();
        Assert.assertEquals(Integer.valueOf(2), queryCounts.get("getRoleByUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("getTeamByUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchRoleAuthByRoleUuidList"));
        Assert.assertEquals(2, result.size());
        UserAuthVo auth = findAuth(result, "process", "BASE");
        Assert.assertTrue(findAuth(result, "process", "DIRECT_ONLY").getSourcePathList().isEmpty());
        Assert.assertEquals("u", auth.getUserUuid());
        Assert.assertEquals("BASE", auth.getAuth());
        Assert.assertEquals("process", auth.getAuthGroup());
        Assert.assertEquals(5, auth.getSourcePathList().size());
        JSONObject envelope = new JSONObject();
        envelope.put("userRoleAuthSourcePathList", result);
        JSONObject serialized = JSONObject.parseObject(envelope.toString(SerializerFeature.DisableCircularReferenceDetect));
        assertSourcePaths(Arrays.asList(source("a", "A角色", null, null, null), source("z", "Z角色", null, null, null),
                source("z", "Z角色", null, null, null),
                source("r", "分组角色", "first", "银行/第一组", 2), source("r", "分组角色", "second", "银行/第二组", 4)), paths(serialized, "process", "BASE"));
        // 返回数组独立于输入记录，调用方后续清空返回来源也不会丢失原始授权来源。
        auth.getSourcePathList().clear();
        Assert.assertEquals(before, mapperSnapshot());
        Assert.assertEquals(5, findAuth(service.getUserAuthListByUserUuid("u"), "process", "BASE").getSourcePathList().size());
    }

    /** 缺少角色名或所属分组路径时过滤对应记录，不生成空来源或伪造说明。 */
    @Test
    public void shouldFilterPermissionsWhenSourceMetadataIsMissing() {
        RoleAuthVo missing = auth("missing", "process", "BASE");
        directAuths.addAll(Arrays.asList(missing, missing));
        TeamVo leaf = team("leaf", "叶分组", null, 2);
        userTeams.add(leaf);
        directTeamRoles.add(role("r", "分组角色", Collections.singletonList(leaf)));
        namedRoles.add(role("r", "分组角色", null));
        teamAuths.add(auth("r", "process", "BASE"));
        // 两种汇总入口都只收到可信来源，缺失资料不会再生成空来源权限记录。
        Assert.assertTrue(service.getUserAuthListByUserUuid("u").isEmpty());
        Assert.assertSame(missing, directAuths.get(0));
        Assert.assertEquals("missing", missing.getRoleUuid());
    }

    /** 有效和无效角色记录混合时，公开结果仅保留有效权限及完整来源，不修改原始记录。 */
    @Test
    public void shouldFilterInvalidRolesAndPreserveTheirValidSources() {
        namedRoles.addAll(Arrays.asList(role("r", "有效角色", null), role("s", "第二角色", null),
                role("empty", "", null), role("blank", "  ", null)));
        RoleAuthVo first = auth("r", "process", "BASE");
        directAuths.addAll(Arrays.asList(auth("missing", "process", "MISSING"), first,
                auth("empty", "process", "EMPTY"), auth("s", "cmdb", "EDIT"),
                auth("blank", "process", "BLANK"), auth(null, "process", "NULL_UUID"),
                auth("  ", "process", "BLANK_UUID"), first));
        List<RoleAuthVo> before = new ArrayList<>(directAuths);
        List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
        Assert.assertEquals(2, result.size());
        UserAuthVo base = findAuth(result, "process", "BASE");
        Assert.assertEquals(Arrays.asList("null/r", "null/r"), sourceIdentities(base));
        Assert.assertEquals(Collections.singletonList("null/s"), sourceIdentities(findAuth(result, "cmdb", "EDIT")));
        assertTrustedRoleAndTeamSources(result);
        Assert.assertNotSame(base.getSourcePathList().get(0), base.getSourcePathList().get(1));
        Assert.assertEquals(before, directAuths);
        Assert.assertSame(first, directAuths.get(1));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("getRoleByUuidList"));
    }

    /** 同角色多分组只保留身份和路径完整的分组；空lft、原始顺序和重复权限记录均保留。 */
    @Test
    public void shouldFilterInvalidTeamSourcesWithoutChangingValidExpansion() {
        TeamVo first = team("first", "第一组", "银行/第一组", 1);
        TeamVo missing = team("missing", "缺失组", null, 2);
        TeamVo blankPath = team("blank-path", "空层级组", "银行/  ", 3);
        TeamVo failedFallback = team("failed", "重建失败组", null, 4);
        TeamVo second = team("second", "第二组", "银行/第二组", 5);
        second.setLft(null);
        // TeamVo默认会为缺失UUID生成新值，边界测试固定返回原始空值，避免误测成有效分组。
        TeamVo blankUuid = new TeamVo() {
            @Override public String getUuid() { return "  "; }
        };
        blankUuid.setUpwardNamePath("银行/空标识组");
        TeamVo nullUuid = new TeamVo() {
            @Override public String getUuid() { return null; }
        };
        nullUuid.setUpwardNamePath("银行/无标识组");
        userTeams.addAll(Arrays.asList(first, missing, blankPath, failedFallback, second, blankUuid, nullUuid));
        detailedTeams.addAll(Arrays.asList(first, blankPath, failedFallback, second, blankUuid, nullUuid));
        namedRoles.addAll(Arrays.asList(role("r", "有效角色", null), role("blank-role", " ", null)));
        directTeamRoles.add(role("r", "有效角色", userTeams));
        directTeamRoles.add(role("missing-role", "未查到角色", Collections.singletonList(first)));
        directTeamRoles.add(role("blank-role", " ", Collections.singletonList(first)));
        RoleAuthVo original = auth("r", "process", "BASE");
        teamAuths.addAll(Arrays.asList(original, auth("missing-role", "process", "MISSING"),
                auth("r", "cmdb", "EDIT"), auth("blank-role", "process", "BLANK"), original));
        List<RoleAuthVo> before = new ArrayList<>(teamAuths);
        List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
        Assert.assertEquals(2, result.size());
        UserAuthVo base = findAuth(result, "process", "BASE");
        Assert.assertEquals(Arrays.asList("first/r", "second/r", "first/r", "second/r"), sourceIdentities(base));
        Assert.assertEquals(Arrays.asList("first/r", "second/r"), sourceIdentities(findAuth(result, "cmdb", "EDIT")));
        assertTrustedRoleAndTeamSources(result);
        Assert.assertNull(base.getSourcePathList().stream().filter(path -> "second".equals(path.getTeamUuid())).findFirst().get().getTeamLft());
        Assert.assertNotSame(base.getSourcePathList().get(0), base.getSourcePathList().get(2));
        Assert.assertEquals(before, teamAuths);
        Assert.assertSame(original, teamAuths.get(0));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchRoleAuthByRoleUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("getRoleByUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("getTeamByUuidList"));
        Assert.assertEquals(1, pathQueryCount);
    }

    /** 过滤无效角色后，相同目标仍可由有效用户直接授权、角色或分组来源提供。 */
    @Test
    public void shouldExpandIncludesFromRemainingTrustedOrigins() throws Exception {
        installGraph();
        namedRoles.add(role("valid", "有效角色", null));
        TeamVo leaf = team("leaf", "有效分组", "银行/有效分组", 2);
        for (String origin : Arrays.asList("user", "role", "team")) {
            userAuths.clear();
            directAuths.clear();
            teamAuths.clear();
            userTeams.clear();
            detailedTeams.clear();
            directTeamRoles.clear();
            directAuths.add(auth("missing", "process", "INC_SECOND_ROOT"));
            if ("user".equals(origin)) {
                userAuths.add(userAuth("process", "INC_ROOT"));
            } else if ("role".equals(origin)) {
                directAuths.add(auth("valid", "process", "INC_ROOT"));
            } else {
                userTeams.add(leaf);
                detailedTeams.add(leaf);
                directTeamRoles.add(role("valid", "有效角色", Collections.singletonList(leaf)));
                teamAuths.add(auth("valid", "process", "INC_ROOT"));
            }
            // 每个有效来源只执行一次公开入口，验证过滤后仍可由该来源展开包含权限。
            List<UserAuthVo> rows = service.getUserAuthListByUserUuid("u");
            UserAuthVo target = findAuth(rows, "cmdb", "INC_TARGET");
            Assert.assertEquals(1, target.getSourcePathList().size());
            SourcePathVo source = target.getSourcePathList().get(0);
            Assert.assertEquals("user".equals(origin) ? null : "valid", source.getRoleUuid());
            Assert.assertEquals("team".equals(origin) ? "leaf" : null, source.getTeamUuid());
            Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"), source.getIncludeAuthPathList().stream()
                    .map(node -> node.getString("auth")).collect(java.util.stream.Collectors.toList()));
            Assert.assertFalse(rows.stream().anyMatch(row -> "INC_SECOND_ROOT".equals(row.getAuth())));
        }
    }

    /** DTO来源字段按真实数组声明接口元数据，已有访问方法保持可用。 */
    @Test
    public void shouldDescribeSourcePathsAsJsonArray() throws Exception {
        Assert.assertEquals(neatlogic.framework.common.constvalue.ApiParamType.JSONARRAY,
                UserAuthVo.class.getDeclaredField("sourcePathList").getAnnotation(neatlogic.framework.restful.annotation.EntityField.class).type());
        UserAuthVo auth = new UserAuthVo();
        List<SourcePathVo> sourcePaths = new ArrayList<>();
        auth.setSourcePathList(sourcePaths);
        Assert.assertSame(sourcePaths, auth.getSourcePathList());
    }

    /** 中间来源仅供内存使用，JSON隐藏单来源，Java序列化仍保留来源对象。 */
    @Test
    public void shouldHideSingleSourceInJsonWithoutLosingJavaSerialization() throws Exception {
        UserAuthVo auth = inheritedAuth("process", "UNREGISTERED_SERIALIZATION_PERMISSION",
                sourceVo("r", "原始角色", "leaf", "银行/叶分组", 2));
        Assert.assertNotNull(auth.getSourcePath());
        Assert.assertNull(auth.getSourcePathList());
        JSONObject wire = JSONObject.parseObject(JSONObject.toJSONString(auth, SerializerFeature.WriteMapNullValue));
        Assert.assertFalse(wire.containsKey("sourcePath"));
        Assert.assertNull(wire.get("sourcePathList"));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(auth);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            UserAuthVo copy = (UserAuthVo) in.readObject();
            Assert.assertEquals("原始角色", copy.getSourcePath().getRoleName());
            Assert.assertEquals(Arrays.asList("银行", "叶分组"), copy.getSourcePath().getTeamPathNameList());
            Assert.assertNull(copy.getSourcePathList());
        }
    }

    /** 普通与包含来源均经真实查询得到；输出复制角色来源，避免同次结果间共享可变数据。 */
    @Test
    public void shouldAggregateQueriedSourcesWithoutSharingMutableState() throws Exception {
        installGraph();
        namedRoles.add(role("r", "角色", null));
        RoleAuthVo raw = auth("r", "process", "INC_ROOT");
        directAuths.addAll(Arrays.asList(raw, raw));
        TeamVo first = team("first", "第一组", "银行/第一组", 2);
        userTeams.add(first);
        detailedTeams.add(first);
        namedRoles.add(role("t", "分组角色", null));
        directTeamRoles.add(role("t", "分组角色", Collections.singletonList(first)));
        teamAuths.add(auth("t", "process", "INC_ROOT"));
        userAuths.add(userAuth("process", "INC_ROOT"));
        String before = mapperSnapshot();
        List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
        UserAuthVo root = findAuth(result, "process", "INC_ROOT");
        Assert.assertEquals(Arrays.asList("null/r", "null/r", "first/t"), sourceIdentities(root));
        UserAuthVo target = findAuth(result, "cmdb", "INC_TARGET");
        Assert.assertEquals(4, target.getSourcePathList().size());
        List<SourcePathVo> roleSources = target.getSourcePathList().stream().filter(path -> "r".equals(path.getRoleUuid()))
                .collect(java.util.stream.Collectors.toList());
        root.getSourcePathList().get(0).setRoleName("修改角色");
        Assert.assertEquals("角色", root.getSourcePathList().get(1).getRoleName());
        roleSources.get(0).getIncludeAuthPathList().get(0).put("auth", "CHANGED");
        Assert.assertEquals("INC_ROOT", roleSources.get(1).getIncludeAuthPathList().get(0).getString("auth"));
        Assert.assertEquals(before, mapperSnapshot());
        assertAuthorizationQueriesOnce();
    }

    /** 独立来源DTO支持完整属性；默认JSON省略空值，JSON及Java序列化均保留有效来源资料。 */
    @Test
    public void shouldRoundTripTypedSourcesThroughJsonAndJavaSerialization() throws Exception {
        SourcePathVo ordinary = new SourcePathVo();
        ordinary.setRoleUuid("r");
        ordinary.setRoleName("<管理员>角色");
        ordinary.setTeamUuid(null);
        ordinary.setTeamPathNameList(new ArrayList<>());
        ordinary.setTeamLft(null);
        ordinary.setIncludeAuthPathList(null);
        Assert.assertNull(ordinary.getIsSuperAdmin());
        SourcePathVo included = sourceVo("r", "<管理员>角色", "leaf", "银行/叶分组", 5);
        JSONObject root = new JSONObject();
        root.put("authGroup", "process");
        root.put("auth", "ROOT");
        JSONObject target = new JSONObject();
        target.put("authGroup", "cmdb");
        target.put("auth", "TARGET");
        included.setIncludeAuthPathList(new ArrayList<>(Arrays.asList(root, target)));
        UserAuthVo auth = new UserAuthVo("u", "UNREGISTERED_SERIALIZATION_PERMISSION");
        auth.setAuthGroup("cmdb");
        // 超级管理员标识同样参与JSON及Java序列化，普通来源仍省略该空字段。
        SourcePathVo superAdmin = new SourcePathVo();
        superAdmin.setIsSuperAdmin(true);
        superAdmin.setTeamPathNameList(new ArrayList<>());
        auth.setSourcePathList(new ArrayList<>(Arrays.asList(ordinary, included, superAdmin)));
        String json = JSONObject.toJSONString(auth, SerializerFeature.DisableCircularReferenceDetect);
        JSONObject wire = JSONObject.parseObject(json);
        JSONObject normal = wire.getJSONArray("sourcePathList").getJSONObject(0);
        Assert.assertEquals(new HashSet<>(Arrays.asList("roleUuid", "roleName", "teamPathNameList")), normal.keySet());
        Assert.assertNull(normal.get("includeAuthPathList"));
        Assert.assertNull(normal.get("teamUuid"));
        Assert.assertNull(normal.get("teamLft"));
        UserAuthVo fromJson = JSONObject.parseObject(json, UserAuthVo.class);
        Assert.assertEquals(Boolean.TRUE, fromJson.getSourcePathList().get(2).getIsSuperAdmin());
        Assert.assertEquals(new HashSet<>(Arrays.asList("isSuperAdmin", "teamPathNameList")),
                wire.getJSONArray("sourcePathList").getJSONObject(2).keySet());
        Assert.assertEquals(SourcePathVo.class, fromJson.getSourcePathList().get(0).getClass());
        Assert.assertEquals("<管理员>角色", fromJson.getSourcePathList().get(0).getRoleName());
        Assert.assertEquals(Arrays.asList("银行", "叶分组"), fromJson.getSourcePathList().get(1).getTeamPathNameList());
        Assert.assertEquals(Integer.valueOf(5), fromJson.getSourcePathList().get(1).getTeamLft());
        Assert.assertEquals("TARGET", fromJson.getSourcePathList().get(1).getIncludeAuthPathList().get(1).getString("auth"));
        Assert.assertEquals(json, JSONObject.toJSONString(fromJson, SerializerFeature.DisableCircularReferenceDetect));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(auth);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            UserAuthVo fromJava = (UserAuthVo) in.readObject();
            Assert.assertEquals(Boolean.TRUE, fromJava.getSourcePathList().get(2).getIsSuperAdmin());
            Assert.assertEquals(json, JSONObject.toJSONString(fromJava, SerializerFeature.DisableCircularReferenceDetect));
            Assert.assertEquals(SourcePathVo.class, fromJava.getSourcePathList().get(1).getClass());
        }
        Assert.assertNull(new UserAuthVo().getSourcePathList());
    }

    /** 缓存模板绑定到直接及分组来源时深复制；修改一个输出不影响其他来源及原始资料。 */
    @Test
    public void shouldIsolateMutableTypedSourcesAndIncludeNodes() throws Exception {
        installGraph();
        userAuths.add(userAuth("process", "INC_ROOT"));
        TeamVo leaf = team("leaf", "叶分组", "银行/叶分组", 2);
        userTeams.add(leaf);
        detailedTeams.add(leaf);
        namedRoles.add(role("r", "管理员", null));
        directTeamRoles.add(role("r", "管理员", Collections.singletonList(leaf)));
        teamAuths.add(auth("r", "process", "INC_ROOT"));
        String before = mapperSnapshot();
        List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
        SourcePathVo normal = findAuth(result, "process", "INC_ROOT").getSourcePathList().get(0);
        List<SourcePathVo> targets = findAuth(result, "cmdb", "INC_TARGET").getSourcePathList();
        SourcePathVo inherited = targets.stream().filter(path -> "r".equals(path.getRoleUuid())).findFirst().get();
        SourcePathVo direct = targets.stream().filter(path -> path.getRoleUuid() == null).findFirst().get();
        Assert.assertNotSame(normal.getTeamPathNameList(), inherited.getTeamPathNameList());
        normal.setRoleName("已修改");
        normal.getTeamPathNameList().add("新增分组");
        inherited.getIncludeAuthPathList().get(0).put("auth", "CHANGED");
        Assert.assertEquals(Arrays.asList("银行", "叶分组"), inherited.getTeamPathNameList());
        Assert.assertEquals("INC_ROOT", direct.getIncludeAuthPathList().get(0).getString("auth"));
        Assert.assertEquals(before, mapperSnapshot());
        Assert.assertEquals(1, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
        Assert.assertEquals("管理员", findAuth(service.getUserAuthListByUserUuid("u"), "process", "INC_ROOT")
                .getSourcePathList().get(0).getRoleName());
    }

    /** 同角色多分组分别展开；重复继承或直接关联去重，Mapper重复权限记录仍完整保留。 */
    @Test
    public void shouldExpandEachRolePermissionByMembershipWithoutChangingRawRows() {
        TeamVo first = team("first", "第一组", "银行/第一组", 2);
        TeamVo second = team("second", "第二组", "银行/第二组", 4);
        userTeams.addAll(Arrays.asList(first, second));
        detailedTeams.addAll(Arrays.asList(first, second));
        namedRoles.add(role("r", "共享角色", null));
        RoleVo shared = role("r", "共享角色", Arrays.asList(first, second));
        ancestorRoles.put("first", Arrays.asList(shared, shared));
        ancestorRoles.put("second", Collections.singletonList(shared));
        directTeamRoles.addAll(Arrays.asList(shared, shared));
        RoleAuthVo original = auth("r", "process", "BASE");
        teamAuths.addAll(Arrays.asList(original, auth("r", "cmdb", "EDIT"), original));
        List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(Arrays.asList("first/r", "second/r", "first/r", "second/r"),
                sourceIdentities(findAuth(result, "process", "BASE")));
        Assert.assertEquals(Arrays.asList("first/r", "second/r"), sourceIdentities(findAuth(result, "cmdb", "EDIT")));
        Assert.assertEquals(3, teamAuths.size());
        Assert.assertSame(original, teamAuths.get(0));
        Assert.assertSame(original, teamAuths.get(2));
        Assert.assertEquals("r", original.getRoleUuid());
        Assert.assertEquals("process", original.getAuthGroup());
        Assert.assertEquals("BASE", original.getAuth());
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchRoleAuthByRoleUuidList"));
    }

    /** 同角色拥有多个根且关联多个分组时，按根和所属分组分别生成真实来源，不串用身份。 */
    @Test
    public void shouldPreserveQueriedTeamAndRootIdentities() throws Exception {
        installGraph();
        TeamVo first = team("first", "第一组", "银行/第一组", 2);
        TeamVo second = team("second", "第二组", "银行/第二组", 4);
        userTeams.addAll(Arrays.asList(first, second));
        detailedTeams.addAll(userTeams);
        namedRoles.addAll(Arrays.asList(role("direct", "直接角色", null), role("r", "共享角色", null)));
        directAuths.add(auth("direct", "process", "INC_ROOT"));
        directTeamRoles.add(role("r", "共享角色", userTeams));
        teamAuths.addAll(Arrays.asList(auth("r", "process", "INC_ROOT"), auth("r", "process", "INC_SECOND_ROOT")));
        String before = mapperSnapshot();
        List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
        Assert.assertEquals(Arrays.asList("null/direct", "first/r", "second/r"),
                sourceIdentities(findAuth(result, "process", "INC_ROOT")));
        List<SourcePathVo> targets = findAuth(result, "cmdb", "INC_TARGET").getSourcePathList();
        Assert.assertEquals(5, targets.size());
        for (String teamUuid : Arrays.asList("first", "second")) {
            List<String> roots = targets.stream().filter(path -> teamUuid.equals(path.getTeamUuid()))
                    .map(path -> path.getIncludeAuthPathList().get(0).getString("auth")).sorted()
                    .collect(java.util.stream.Collectors.toList());
            Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_SECOND_ROOT"), roots);
        }
        Assert.assertEquals(1, targets.stream().filter(path -> path.getTeamUuid() == null).count());
        Assert.assertEquals(before, mapperSnapshot());
        Assert.assertEquals(1, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
        assertAuthorizationQueriesOnce();
    }

    /** 空白用户不查询，真实查询返回null或空列表时公开入口返回空集合。 */
    @Test
    public void shouldHandleBlankUsersAndEmptyInputs() {
        for (String userUuid : Arrays.asList(null, "", "  ")) {
            Assert.assertTrue(service.getUserAuthListByUserUuid(userUuid).isEmpty());
        }
        Assert.assertTrue(queryCounts.isEmpty());
        Assert.assertTrue(service.getUserAuthListByUserUuid("u").isEmpty());
        returnNullAuths = true;
        Assert.assertTrue(service.getUserAuthListByUserUuid("u").isEmpty());
        TeamVo leaf = team("leaf", "叶分组", "银行/叶分组", 2);
        userTeams.add(leaf);
        directTeamRoles.add(role("r", "管理员", Collections.singletonList(leaf)));
        Assert.assertTrue(service.getUserAuthListByUserUuid("u").isEmpty());
    }

    /** 用户、多个角色和多个分组共享同根时只展开一次，执行真实批量查询并保留全部来源。 */
    @Test
    public void shouldCacheIncludeDefinitionsAcrossDistinctOrigins() throws Exception {
        installGraph();
        userAuths.add(userAuth("process", "INC_ROOT"));
        namedRoles.addAll(Arrays.asList(role("r1", "第一角色", null), role("r2", "第二角色", null), role("t", "分组角色", null)));
        directAuths.addAll(Arrays.asList(auth("r1", "process", "INC_ROOT"), auth("r2", "process", "INC_ROOT")));
        TeamVo first = team("first", "第一组", "银行/第一组", 2);
        TeamVo second = team("second", "第二组", "银行/第二组", 4);
        userTeams.addAll(Arrays.asList(first, second));
        detailedTeams.addAll(userTeams);
        directTeamRoles.add(role("t", "分组角色", userTeams));
        teamAuths.add(auth("t", "process", "INC_ROOT"));
        String before = mapperSnapshot();
        List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
        Assert.assertEquals(1, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
        Assert.assertEquals(5, findAuth(result, "cmdb", "INC_TARGET").getSourcePathList().size());
        for (UserAuthVo row : result) {
            Assert.assertEquals("u", row.getUserUuid());
            Assert.assertNull(row.getSourcePath());
            Assert.assertNotNull(row.getSourcePathList());
        }
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"), includedPath(result, "INC_ROOT", "cmdb", "INC_TARGET")
                .stream().map(node -> node.getString("auth")).collect(java.util.stream.Collectors.toList()));
        Assert.assertEquals(before, mapperSnapshot());
        assertAuthorizationQueriesOnce();
        Assert.assertEquals(Integer.valueOf(2), queryCounts.get("getRoleByUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("getTeamByUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchRoleAuthByRoleUuidList"));
    }

    /** 多个角色及所属分组来源完整保留，不同权限组相互独立，不要求来源顺序。 */
    @Test
    public void shouldMergeDirectAndTeamSourcesWithCompatibleStructure() throws Exception {
        namedRoles.add(role("z", "管理员", null));
        namedRoles.add(role("a", "值班角色", null));
        directAuths.add(auth("z", "process", "BASE"));
        directAuths.add(auth("a", "process", "BASE"));
        directAuths.add(auth("a", "cmdb", "BASE"));
        TeamVo root = team("root", "苏州银行", "苏州银行", 1);
        TeamVo leaf = team("leaf", "金融同业部", "苏州银行/金融市场总部/金融同业部", 3);
        userTeams.addAll(Arrays.asList(root, leaf));
        detailedTeams.addAll(userTeams);
        directTeamRoles.add(role("z", "管理员", Arrays.asList(root, leaf)));
        ancestorRoles.put("leaf", Collections.singletonList(role("z", "管理员", Collections.singletonList(root))));
        teamAuths.add(auth("z", "process", "BASE"));
        userAuths.add(userAuth("process", "BASE"));
        userAuths.add(userAuth("process", "EDIT"));
        JSONObject result = search();
        assertSourcePaths(Arrays.asList(source("a", "值班角色", null, null, null),
                source("z", "管理员", null, null, null), source("z", "管理员", "root", "苏州银行", 1),
                source("z", "管理员", "leaf", "苏州银行/金融市场总部/金融同业部", 3)), paths(result, "process", "BASE"));
        Assert.assertEquals(Collections.singletonList(source("a", "值班角色", null, null, null)), paths(result, "cmdb", "BASE"));
        Assert.assertEquals(2, roleNameQueryCount);
        Assert.assertEquals(0, pathQueryCount);
    }

    /** 上级角色使用用户所属叶分组路径，缺失缓存路径时每个分组只重建一次。 */
    @Test
    public void shouldUseMembershipPathForAncestorRolesAndCacheFallback() throws Exception {
        TeamVo root = team("root", "苏州银行", null, 1);
        TeamVo leaf = team("leaf", "金融同业部", null, 2);
        leaf.setParentUuid("root");
        userTeams.add(leaf);
        detailedTeams.addAll(Arrays.asList(leaf, leaf));
        ancestors.put(2, Arrays.asList(root, leaf));
        namedRoles.add(role("r", "系统_管理员", null));
        ancestorRoles.put("leaf", Collections.singletonList(role("r", "系统_管理员", Collections.singletonList(root))));
        teamAuths.add(auth("r", "process", "BASE"));
        teamAuths.add(auth("r", "process", "EDIT"));
        JSONObject result = search();
        Assert.assertEquals(Collections.singletonList(source("r", "系统_管理员", "leaf", "苏州银行/金融同业部", 2)),
                paths(result, "process", "BASE"));
        Assert.assertEquals(paths(result, "process", "BASE"), paths(result, "process", "EDIT"));
        Assert.assertEquals(1, pathQueryCount);
    }

    /** 缺失角色名、分组路径或完整祖先链时过滤记录，不拼造来源。 */
    @Test
    public void shouldOmitUntrustworthySources() throws Exception {
        directAuths.add(auth("missing", "process", "DIRECT"));
        namedRoles.add(role("r", "管理员", null));
        TeamVo leaf = team("leaf", "叶分组", null, 3);
        TeamVo wrongParent = team("wrong", "无关分组", null, 1);
        leaf.setParentUuid("missing-parent");
        userTeams.add(leaf);
        detailedTeams.add(leaf);
        ancestors.put(3, Arrays.asList(wrongParent, leaf));
        directTeamRoles.add(role("r", "管理员", Collections.singletonList(leaf)));
        teamAuths.add(auth("r", "process", "BASE"));
        JSONObject result = search();
        Assert.assertTrue(result.getJSONArray("userRoleAuthSourcePathList").isEmpty());
    }

    /** 没有角色或包含关系时，用户直接权限正常返回，来源列表为空。 */
    @Test
    public void shouldReturnDirectPermissionWithEmptySources() throws Exception {
        userAuths.add(userAuth("process", "EDIT"));
        List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(findAuth(result, "process", "EDIT").getSourcePathList().isEmpty());
        Assert.assertEquals(0, roleNameQueryCount);
    }

    /** 查询缺少最顶层祖先时，即使末端连续也不能把残缺路径返回给页面。 */
    @Test
    public void shouldRejectPartialAncestorChain() throws Exception {
        TeamVo leaf = team("leaf", "金融同业部", null, 3);
        leaf.setParentUuid("missing-root");
        userTeams.add(leaf);
        detailedTeams.add(leaf);
        ancestors.put(3, Collections.singletonList(leaf));
        namedRoles.add(role("r", "管理员", null));
        directTeamRoles.add(role("r", "管理员", Collections.singletonList(leaf)));
        teamAuths.add(auth("r", "process", "BASE"));
        JSONObject result = search();
        Assert.assertTrue(result.getJSONArray("userRoleAuthSourcePathList").isEmpty());
    }

    /** 角色与分组名称及特殊字符原样返回，不增删中文后缀。 */
    @Test
    public void shouldPreserveNamesWithSuffixesAndSpecialCharacters() throws Exception {
        TeamVo team = team("t", "<银行>分组", "<银行>分组", 1);
        userTeams.add(team);
        detailedTeams.add(team);
        namedRoles.add(role("r", "系统_管理员角色", null));
        directTeamRoles.add(role("r", "系统_管理员角色", Collections.singletonList(team)));
        teamAuths.add(auth("r", "process", "BASE"));
        Assert.assertEquals(Collections.singletonList(source("r", "系统_管理员角色", "t", "<银行>分组", 1)),
                paths(search(), "process", "BASE"));
    }

    /** 同名不同UUID的角色和分组来源分别保留，缺失lft也保持原始来源资料。 */
    @Test
    public void shouldKeepDistinctIdentitiesAndNullableTeamLft() throws Exception {
        namedRoles.add(role("z", "相同角色", null));
        namedRoles.add(role("a", "相同角色", null));
        directAuths.add(auth("z", "process", "BASE"));
        directAuths.add(auth("a", "process", "BASE"));
        TeamVo early = team("z-early", "Z分组", "Z分组", 1);
        TeamVo late = team("a-late", "A分组", "A分组", 2);
        TeamVo sameLeft = team("b-late", "B分组", "B分组", 2);
        TeamVo missingLeft = team("a-null", "C分组", "C分组", 3);
        missingLeft.setLft(null);
        TeamVo secondMissingLeft = team("b-null", "D分组", "D分组", 4);
        secondMissingLeft.setLft(null);
        userTeams.addAll(Arrays.asList(secondMissingLeft, sameLeft, late, missingLeft, early));
        detailedTeams.addAll(userTeams);
        directTeamRoles.add(role("z", "相同角色", userTeams));
        directTeamRoles.add(role("a", "相同角色", Collections.singletonList(late)));
        teamAuths.add(auth("z", "process", "BASE"));
        teamAuths.add(auth("a", "process", "BASE"));
        List<JSONObject> rows = paths(search(), "process", "BASE");
        assertSourcePaths(Arrays.asList(source("a", "相同角色", null, null, null),
                source("z", "相同角色", null, null, null), source("z", "相同角色", "z-early", "Z分组", 1),
                source("a", "相同角色", "a-late", "A分组", 2), source("z", "相同角色", "a-late", "A分组", 2),
                source("z", "相同角色", "b-late", "B分组", 2), source("z", "相同角色", "a-null", "C分组", null),
                source("z", "相同角色", "b-null", "D分组", null)), rows);
        JSONObject missingLeftSource = rows.stream().filter(path -> "a-null".equals(path.getString("teamUuid"))).findFirst().get();
        Assert.assertFalse(missingLeftSource.containsKey("teamLft"));
        Assert.assertNull(missingLeftSource.get("teamLft"));
    }

    /** 用户直接根递归包含跨组权限，传入的重复直接授权记录保持原样。 */
    @Test
    public void shouldExpandDirectIncludesAndPreserveDirectTargets() throws Exception {
        installGraph();
        userAuths.add(userAuth("process", "INC_ROOT"));
        userAuths.add(userAuth("cmdb", "INC_TARGET"));
        JSONObject result = search();
        Assert.assertEquals(2, userAuths.size());
        Assert.assertEquals("INC_TARGET", userAuths.get(1).getAuth());
        JSONObject included = paths(result, "cmdb", "INC_TARGET").get(0);
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"), includeNames(included));
        Assert.assertFalse(included.containsKey("roleUuid"));
        Assert.assertNull(included.get("roleUuid"));
        Assert.assertFalse(included.containsKey("teamUuid"));
        Assert.assertNull(included.get("teamUuid"));
        Assert.assertEquals(2, paths(result, "autoexec", "INC_DEEP").size());
    }

    /** 普通来源和用户、角色、分组的包含来源均保留，按身份定位而非要求旧排序。 */
    @Test
    public void shouldPreserveOrdinaryAndIncludedSourcesForAllOrigins() throws Exception {
        installGraph();
        userAuths.add(userAuth("process", "INC_ROOT"));
        namedRoles.add(role("r", "根角色", null));
        namedRoles.add(role("legacy", "目标角色", null));
        directAuths.add(auth("r", "process", "INC_ROOT"));
        directAuths.add(auth("legacy", "cmdb", "INC_TARGET"));
        TeamVo leaf = team("leaf", "叶分组", "银行/叶分组", 2);
        userTeams.add(leaf);
        detailedTeams.add(leaf);
        ancestorRoles.put("leaf", Collections.singletonList(role("r", "根角色", null)));
        teamAuths.add(auth("r", "process", "INC_ROOT"));
        List<JSONObject> rows = paths(search(), "cmdb", "INC_TARGET");
        Assert.assertEquals(4, rows.size());
        JSONObject ordinary = rows.stream().filter(path -> path.getJSONArray("includeAuthPathList") == null).findFirst().get();
        Assert.assertEquals(source("legacy", "目标角色", null, null, null), ordinary);
        Assert.assertFalse(ordinary.containsKey("includeAuthPathList"));
        JSONObject user = rows.stream().filter(path -> path.getString("roleUuid") == null).findFirst().get();
        JSONObject role = rows.stream().filter(path -> "r".equals(path.getString("roleUuid"))
                && path.getString("teamUuid") == null).findFirst().get();
        JSONObject team = rows.stream().filter(path -> "leaf".equals(path.getString("teamUuid"))).findFirst().get();
        for (JSONObject path : Arrays.asList(user, role, team)) {
            Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"), includeNames(path));
        }
        Assert.assertEquals(Arrays.asList("银行", "叶分组"), team.getJSONArray("teamPathNameList"));
    }

    /** 资料缺失的角色记录被查询方法过滤，不再作为包含展开的根。 */
    @Test
    public void shouldSkipIncludeRootsWithoutTrustedRoleMetadata() throws Exception {
        installGraph();
        directAuths.add(auth("missing", "process", "INC_ROOT"));
        Assert.assertTrue(search().getJSONArray("userRoleAuthSourcePathList").isEmpty());
        Assert.assertEquals(0, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
    }

    /** 每根每目标仅保留最短链，等长菱形链选编码较小分支，循环、自包含及未注册节点跳过。 */
    @Test
    public void shouldChooseShortestStableChainsAndRejectCycles() throws Exception {
        installGraph();
        userAuths.add(userAuth("process", "INC_ROOT"));
        userAuths.add(userAuth("process", "INC_SHORT_ROOT"));
        userAuths.add(userAuth("process", "INC_UNREGISTERED"));
        JSONObject result = search();
        List<JSONObject> rows = paths(result, "cmdb", "INC_TARGET");
        Assert.assertEquals(2, rows.size());
        Map<String, List<String>> byRoot = new HashMap<>();
        rows.forEach(path -> byRoot.put(includeNames(path).get(0), includeNames(path)));
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"), byRoot.get("INC_ROOT"));
        Assert.assertEquals(Arrays.asList("INC_SHORT_ROOT", "INC_TARGET"), byRoot.get("INC_SHORT_ROOT"));
        // ROOT只会因为SHORT_ROOT包含它而只读，不允许ROOT自己产生ROOT->ROOT来源。
        List<JSONObject> rootRows = paths(result, "process", "INC_ROOT");
        Assert.assertEquals(1, rootRows.size());
        Assert.assertEquals(Arrays.asList("INC_SHORT_ROOT", "INC_ROOT"), includeNames(rootRows.get(0)));
    }

    /** 同一个角色的多个不同根权限产生独立包含来源，不按目标全局合并。 */
    @Test
    public void shouldKeepDistinctIncludeRootsForSameRole() throws Exception {
        installGraph();
        namedRoles.add(role("r", "管理员", null));
        directAuths.add(auth("r", "process", "INC_SECOND_ROOT"));
        directAuths.add(auth("r", "process", "INC_ROOT"));
        List<JSONObject> rows = paths(search(), "cmdb", "INC_TARGET");
        Assert.assertEquals(2, rows.size());
        List<String> roots = rows.stream().map(path -> path.getJSONArray("includeAuthPathList").getJSONObject(0).getString("auth"))
                .sorted().collect(java.util.stream.Collectors.toList());
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_SECOND_ROOT"), roots);
    }

    /** 用户直接权限自身允许空来源；递归展开的目标通过新入口返回完整来源。 */
    @Test
    public void shouldReturnDirectPermissionsAndIncludedSources() throws Exception {
        installGraph();
        for (String root : Arrays.asList("INC_DEEP", "INC_ROOT")) {
            userAuths.clear();
            userAuths.add(userAuth("INC_DEEP".equals(root) ? "autoexec" : "process", root));
            List<UserAuthVo> result = service.getUserAuthListByUserUuid("u");
            Assert.assertEquals("INC_DEEP".equals(root) ? 1 : 5, result.size());
            Assert.assertTrue(findAuth(result, userAuths.get(0).getAuthGroup(), root).getSourcePathList().isEmpty());
            if ("INC_ROOT".equals(root)) {
                Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"),
                        includedPath(result, root, "cmdb", "INC_TARGET").stream()
                                .map(node -> node.getString("auth")).collect(java.util.stream.Collectors.toList()));
            }
        }
    }

    /** 真实查询过滤资料缺失记录后，新入口不产生相应根权限和包含权限的空来源记录。 */
    @Test
    public void shouldOmitMissingMetadataRowsFromAggregation() throws Exception {
        installGraph();
        directAuths.add(auth("missing", "process", "INC_ROOT"));
        TeamVo leaf = team("leaf", "叶分组", null, 2);
        userTeams.add(leaf);
        namedRoles.add(role("t", "分组角色", null));
        directTeamRoles.add(role("t", "分组角色", Collections.singletonList(leaf)));
        teamAuths.add(auth("t", "process", "INC_ROOT"));
        Assert.assertTrue(service.getUserAuthListByUserUuid("u").isEmpty());
        Assert.assertEquals(0, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
    }

    /** 只替换测试注册项并在结束时恢复，不影响其他授权回归测试的静态注册表。 */
    @SuppressWarnings("unchecked")
    private void installGraph() throws Exception {
        Field field = AuthFactory.class.getDeclaredField("authMap");
        field.setAccessible(true);
        registry = (Map<String, AuthBase>) field.get(null);
        registryBackup = new HashMap<>(registry);
        for (AuthBase auth : Arrays.asList(new INC_ROOT(), new INC_LEFT(), new INC_RIGHT(), new INC_TARGET(),
                new INC_DEEP(), new INC_SECOND_ROOT(), new INC_SHORT_ROOT())) {
            registry.put(auth.getAuthName(), auth);
        }
        registry.remove("INC_UNREGISTERED");
    }

    @After
    public void restoreAuthRegistry() {
        if (registryBackup != null) {
            registry.clear();
            registry.putAll(registryBackup);
        }
    }

    private List<String> includeNames(JSONObject row) {
        List<String> names = new ArrayList<>();
        JSONArray path = row.getJSONArray("includeAuthPathList");
        for (int i = 0; i < path.size(); i++) {
            names.add(path.getJSONObject(i).getString("auth"));
        }
        return names;
    }

    /** 合成最小权限图，使用真实AuthFactory注册实例验证遍历而不依赖业务模块。 */
    public abstract static class IncludeTestAuth extends AuthBase {
        @Override public String getAuthDisplayName() { return getAuthName(); }
        @Override public String getAuthIntroduction() { return getAuthName(); }
        @Override public String getAuthGroup() { return "process"; }
        @Override public Integer getSort() { return 0; }
    }
    public static class INC_ROOT extends IncludeTestAuth {
        // 记录真实注册实例的读取次数，验证重复数据库根去重及来源组装复用缓存链。
        private int includeReadCount;
        @Override public List<Class<? extends AuthBase>> getIncludeAuths() {
            includeReadCount++;
            return Arrays.asList(INC_RIGHT.class, INC_LEFT.class, INC_ROOT.class, INC_UNREGISTERED.class);
        }
    }
    public static class INC_LEFT extends IncludeTestAuth {
        @Override public List<Class<? extends AuthBase>> getIncludeAuths() { return Arrays.asList(INC_TARGET.class, INC_ROOT.class); }
    }
    public static class INC_RIGHT extends IncludeTestAuth {
        @Override public List<Class<? extends AuthBase>> getIncludeAuths() { return Collections.singletonList(INC_TARGET.class); }
    }
    public static class INC_TARGET extends IncludeTestAuth {
        @Override public String getAuthGroup() { return "cmdb"; }
        @Override public List<Class<? extends AuthBase>> getIncludeAuths() { return Collections.singletonList(INC_DEEP.class); }
    }
    public static class INC_DEEP extends IncludeTestAuth {
        @Override public String getAuthGroup() { return "autoexec"; }
    }
    public static class INC_SECOND_ROOT extends IncludeTestAuth {
        @Override public List<Class<? extends AuthBase>> getIncludeAuths() { return Collections.singletonList(INC_TARGET.class); }
    }
    public static class INC_SHORT_ROOT extends IncludeTestAuth {
        @Override public List<Class<? extends AuthBase>> getIncludeAuths() { return Arrays.asList(INC_ROOT.class, INC_TARGET.class); }
    }
    public static class INC_UNREGISTERED extends IncludeTestAuth { }

    /** 独立模块权限用于验证超级管理员分支，不依赖实际业务模块的注册状态。 */
    public static class SUPER_SOURCE_A extends IncludeTestAuth {
        private int includeReadCount;
        @Override public String getAuthGroup() { return "super-admin-source-active"; }
        @Override public List<Class<? extends AuthBase>> getIncludeAuths() {
            includeReadCount++;
            return Collections.singletonList(SUPER_SOURCE_B.class);
        }
    }
    /** 同激活模块的第二条权限用于验证来源对象互不共享。 */
    public static class SUPER_SOURCE_B extends IncludeTestAuth {
        @Override public String getAuthGroup() { return "super-admin-source-active"; }
    }
    /** 未激活模块权限即便已注册也不应返回。 */
    public static class SUPER_SOURCE_DISABLED extends IncludeTestAuth {
        @Override public String getAuthGroup() { return "super-admin-source-disabled"; }
    }

    /** 按当前DTO默认序列化配置构造普通来源预期：空值字段省略，名称和路径保持原值。 */
    private JSONObject source(String roleUuid, String roleName, String teamUuid, String path, Integer lft) {
        JSONObject row = new JSONObject();
        if (roleUuid != null) {
            row.put("roleUuid", roleUuid);
        }
        if (roleName != null) {
            row.put("roleName", roleName);
        }
        if (teamUuid != null) {
            row.put("teamUuid", teamUuid);
        }
        row.put("teamPathNameList", path == null ? Collections.emptyList() : Arrays.asList(path.split("/", -1)));
        if (lft != null) {
            row.put("teamLft", lft);
        }
        return row;
    }

    /** 从最终结果按目标和包含根定位完整链，不依赖来源列表顺序。 */
    private List<JSONObject> includedPath(List<UserAuthVo> authList, String root, String group, String auth) {
        for (SourcePathVo source : findAuth(authList, group, auth).getSourcePathList()) {
            List<JSONObject> chain = source.getIncludeAuthPathList();
            if (chain != null && !chain.isEmpty() && root.equals(chain.get(0).getString("auth"))) {
                return chain;
            }
        }
        throw new AssertionError("缺少包含链：" + root + " -> " + group + "/" + auth);
    }

    /** 从公开聚合结果读取来源身份，保留同一权限下原始来源的相对顺序及重复项。 */
    private List<String> sourceIdentities(UserAuthVo auth) {
        return auth.getSourcePathList().stream().map(path -> path.getTeamUuid() + "/" + path.getRoleUuid())
                .collect(java.util.stream.Collectors.toList());
    }

    /** 仅含角色及分组输入的结果应携带完整来源，聚合对象不暴露单来源字段。 */
    private void assertTrustedRoleAndTeamSources(List<UserAuthVo> auths) {
        for (UserAuthVo auth : auths) {
            Assert.assertEquals("u", auth.getUserUuid());
            Assert.assertNull(auth.getSourcePath());
            Assert.assertFalse(auth.getSourcePathList().isEmpty());
            for (SourcePathVo path : auth.getSourcePathList()) {
                Assert.assertFalse(StringUtils.isBlank(path.getRoleUuid()));
                Assert.assertFalse(StringUtils.isBlank(path.getRoleName()));
                if (path.getTeamUuid() != null) {
                    Assert.assertFalse(StringUtils.isBlank(path.getTeamUuid()));
                    Assert.assertFalse(path.getTeamPathNameList().isEmpty());
                    Assert.assertFalse(path.getTeamPathNameList().stream().anyMatch(StringUtils::isBlank));
                }
            }
        }
    }

    /** 三类原始授权各查询一次；Mapper代理的白名单同时禁止任何写入或额外查询。 */
    private void assertAuthorizationQueriesOnce() {
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("getUserByUuid"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchUserAuthByUserUuid"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchUserRoleAuthByUserUuid"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("getTeamListByUserUuid"));
    }

    /** 比较完整来源的多重集合，忽略顺序但保留重复项和null字段，不改变调用方列表。 */
    private void assertSourcePaths(List<JSONObject> expected, List<JSONObject> actual) {
        List<String> expectedValues = new ArrayList<>();
        List<String> actualValues = new ArrayList<>();
        expected.forEach(path -> expectedValues.add(JSONObject.toJSONString(path,
                SerializerFeature.MapSortField, SerializerFeature.WriteMapNullValue)));
        actual.forEach(path -> actualValues.add(JSONObject.toJSONString(path,
                SerializerFeature.MapSortField, SerializerFeature.WriteMapNullValue)));
        Collections.sort(expectedValues);
        Collections.sort(actualValues);
        Assert.assertEquals(expectedValues, actualValues);
    }

    /** 真实Mapper资料的完整快照，用于验证聚合和修改输出均不改变原始查询对象。 */
    private String mapperSnapshot() {
        return JSONObject.toJSONString(Arrays.asList(userAuths, directAuths, teamAuths, userTeams, detailedTeams,
                directTeamRoles, namedRoles, ancestorRoles, ancestors),
                SerializerFeature.WriteMapNullValue, SerializerFeature.DisableCircularReferenceDetect);
    }

    /** 按权限组和编码定位，保留跨组同编码的独立身份。 */
    private UserAuthVo findAuth(List<UserAuthVo> auths, String group, String code) {
        return auths.stream().filter(auth -> group.equals(auth.getAuthGroup()) && code.equals(auth.getAuth()))
                .findFirst().orElseThrow(() -> new AssertionError("缺少权限：" + group + "/" + code));
    }

    /** 单参数入口自行查询并计算，辅助方法仅按实际HTTP序列化配置包装结果。 */
    private JSONObject search() {
        String userUuid = "service-auth-test-ordinary-user";
        JSONObject envelope = new JSONObject();
        envelope.put("userRoleAuthSourcePathList", service.getUserAuthListByUserUuid(userUuid));
        return JSONObject.parseObject(envelope.toString(SerializerFeature.DisableCircularReferenceDetect));
    }

    private List<JSONObject> paths(JSONObject result, String group, String auth) {
        JSONArray rows = result.getJSONArray("userRoleAuthSourcePathList");
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.getJSONObject(i);
            if (group.equals(row.getString("authGroup")) && auth.equals(row.getString("auth"))) {
                return row.getJSONArray("sourcePathList").toJavaList(JSONObject.class);
            }
        }
        Assert.fail("缺少来源：" + group + "/" + auth);
        return Collections.emptyList();
    }

    private void inject(String fieldName, Object mapper) throws Exception {
        Field field = UserServiceImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, mapper);
    }

    /** 类型化测试来源只保存原始业务信息，JSON期望仍由独立的source帮助方法构造。 */
    private SourcePathVo sourceVo(String roleUuid, String roleName, String teamUuid, String path, Integer lft) {
        return new SourcePathVo(roleUuid, roleName, teamUuid,
                path == null ? Collections.emptyList() : Arrays.asList(path.split("/", -1)), lft);
    }

    /** 手工构造仅携带单来源的中间记录，不查询或复制生产的分组展开算法。 */
    private UserAuthVo inheritedAuth(String group, String name, SourcePathVo... paths) {
        UserAuthVo auth = new UserAuthVo("u", name);
        auth.setAuthGroup(group);
        Assert.assertTrue("中间记录至多携带一个来源", paths.length <= 1);
        auth.setSourcePath(paths.length == 0 ? null : paths[0]);
        return auth;
    }

    private RoleAuthVo auth(String roleUuid, String group, String name) {
        RoleAuthVo auth = new RoleAuthVo(roleUuid, name);
        auth.setAuthGroup(group);
        return auth;
    }

    private UserAuthVo userAuth(String group, String name) {
        UserAuthVo auth = new UserAuthVo();
        auth.setAuthGroup(group);
        auth.setAuth(name);
        return auth;
    }

    private RoleVo role(String uuid, String name, List<TeamVo> teams) {
        RoleVo role = new RoleVo();
        role.setUuid(uuid);
        role.setName(name);
        if (teams != null) {
            role.setTeamList(teams);
        }
        return role;
    }

    private TeamVo team(String uuid, String name, String path, int left) {
        TeamVo team = new TeamVo();
        team.setUuid(uuid);
        team.setName(name);
        team.setParentUuid(TeamVo.ROOT_UUID);
        team.setUpwardNamePath(path);
        team.setLft(left);
        team.setRht(10 - left);
        return team;
    }
}
