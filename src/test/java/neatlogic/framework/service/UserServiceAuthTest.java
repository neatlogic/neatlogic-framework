package neatlogic.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.auth.core.AuthFactory;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.RoleAuthVo;
import neatlogic.framework.dto.SourcePathVo;
import neatlogic.framework.dto.RoleVo;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.dto.UserAuthVo;
import org.junit.Assert;
import org.junit.After;
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
    private boolean authQueriesForbidden;
    private boolean returnNullAuths;
    private UserServiceImpl service;
    private Map<String, AuthBase> registry;
    private Map<String, AuthBase> registryBackup;

    /** 仅允许预期读取方法，发现额外写入或逐权限查询时使测试失败。 */
    @Before
    public void setup() throws Exception {
        service = new UserServiceImpl();
        inject("userMapper", Proxy.newProxyInstance(UserMapper.class.getClassLoader(), new Class<?>[]{UserMapper.class},
                (proxy, method, args) -> {
                    // 单参数入口允许预期的批量读取；固定输入的聚合边界测试禁止访问Mapper。
                    queryCounts.merge(method.getName(), 1, Integer::sum);
                    queryArgs.put(method.getName(), args);
                    if (authQueriesForbidden) {
                        throw new AssertionError("固定输入的聚合测试不应访问Mapper：" + method.getName());
                    }
                    switch (method.getName()) {
                        case "searchUserAuthByUserUuid": return returnNullAuths ? null : userAuths;
                        case "searchUserRoleAuthByUserUuid": return returnNullAuths ? null : directAuths;
                        default: throw new AssertionError(method.getName());
                    }
                }));
        inject("teamMapper", Proxy.newProxyInstance(TeamMapper.class.getClassLoader(), new Class<?>[]{TeamMapper.class},
                (proxy, method, args) -> {
                    // 单参数入口允许预期的批量读取；固定输入的聚合边界测试禁止访问Mapper。
                    queryCounts.merge(method.getName(), 1, Integer::sum);
                    queryArgs.put(method.getName(), args);
                    if (authQueriesForbidden) {
                        throw new AssertionError("固定输入的聚合测试不应访问Mapper：" + method.getName());
                    }
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
                    // 单参数入口允许预期的批量读取；固定输入的聚合边界测试禁止访问Mapper。
                    queryCounts.merge(method.getName(), 1, Integer::sum);
                    queryArgs.put(method.getName(), args);
                    if (authQueriesForbidden) {
                        throw new AssertionError("固定输入的聚合测试不应访问Mapper：" + method.getName());
                    }
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

    /** 三类列表保留原始记录和角色身份；分组授权同时读取自身角色与允许继承的祖先角色。 */
    @Test
    public void shouldReturnRawAuthorizationListsWithoutExpansionOrFiltering() throws Exception {
        installGraph();
        UserAuthVo rootAuth = userAuth("process", "INC_ROOT");
        userAuths.add(rootAuth);
        userAuths.add(rootAuth);
        RoleAuthVo direct = auth("r", "process", "INC_ROOT");
        directAuths.add(direct);
        directAuths.add(direct);
        TeamVo leaf = team("leaf", "叶分组", "银行/叶分组", 2);
        userTeams.add(leaf);
        detailedTeams.add(leaf);
        namedRoles.addAll(Arrays.asList(role("r", "直接角色", null), role("ancestor", "祖先角色", null), role("own", "分组角色", null)));
        ancestorRoles.put("leaf", Collections.singletonList(role("ancestor", "祖先角色", null)));
        directTeamRoles.add(role("own", "分组角色", Collections.singletonList(leaf)));
        teamAuths.add(auth("ancestor", "process", "INC_ROOT"));
        teamAuths.add(auth("own", "process", "INC_SECOND_ROOT"));
        teamAuths.add(teamAuths.get(0));
        Assert.assertSame(userAuths, service.getUserAuthListByUserUuid("u"));
        List<UserAuthVo> roleResult = service.getRoleAuthListByUserUuid("u");
        Assert.assertEquals(2, roleResult.size());
        Assert.assertEquals("u", roleResult.get(0).getUserUuid());
        Assert.assertEquals("INC_ROOT", roleResult.get(0).getAuth());
        Assert.assertEquals("process", roleResult.get(0).getAuthGroup());
        Assert.assertEquals("r", roleResult.get(0).getSourcePath().getRoleUuid());
        Assert.assertNotNull(roleResult.get(1).getSourcePath());
        Assert.assertNull(roleResult.get(1).getSourcePathList());
        roleResult.forEach(row -> Assert.assertNull(row.getSourcePathList()));
        List<UserAuthVo> result = service.getTeamAuthListByUserUuid("u");
        Assert.assertEquals(Arrays.asList("leaf/ancestor/process/INC_ROOT", "leaf/own/process/INC_SECOND_ROOT", "leaf/ancestor/process/INC_ROOT"), teamAuthKeys(result));
        Assert.assertEquals(UserAuthVo.class, result.get(0).getClass());
        Assert.assertEquals("ancestor", teamAuths.get(0).getRoleUuid());
        Assert.assertEquals("process", teamAuths.get(0).getAuthGroup());
        Assert.assertEquals("INC_ROOT", teamAuths.get(0).getAuth());
        Assert.assertEquals(Arrays.asList("ancestor", "own"), queryArgs.get("searchRoleAuthByRoleUuidList")[0]);
        Assert.assertEquals("u", queryArgs.get("searchUserAuthByUserUuid")[0]);
        Assert.assertEquals("u", queryArgs.get("searchUserRoleAuthByUserUuid")[0]);
        Assert.assertEquals(0, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
        Assert.assertEquals(2, userAuths.size());
        Assert.assertEquals(2, directAuths.size());
        Assert.assertEquals(3, teamAuths.size());
    }

    /** 角色查询保留原始粒度，最终来源按权限聚合全部角色和分组，不混入单纯直接授权。 */
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
        List<UserAuthVo> roleList = service.getRoleAuthListByUserUuid("u");
        List<UserAuthVo> teamList = service.getTeamAuthListByUserUuid("u");
        Assert.assertEquals(3, roleList.size());
        Assert.assertEquals(Arrays.asList("z", "a", "z"), roleList.stream()
                .map(auth -> auth.getSourcePath().getRoleUuid())
                .collect(java.util.stream.Collectors.toList()));
        Assert.assertEquals(2, teamList.size());
        Assert.assertNotSame(roleList.get(0).getSourcePath(), roleList.get(2).getSourcePath());
        Assert.assertNotSame(teamList.get(0).getSourcePath(), teamList.get(1).getSourcePath());
        // 单参数入口自行查询三类授权，检查每类查询只执行一次。
        userAuths.add(userAuth("process", "DIRECT_ONLY"));
        queryCounts.clear();
        List<UserAuthVo> result = service.getAuthSourcePathList("u");
        assertAuthorizationQueriesOnce();
        Assert.assertEquals(Integer.valueOf(2), queryCounts.get("getRoleByUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("getTeamByUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchRoleAuthByRoleUuidList"));
        Assert.assertEquals(1, result.size());
        UserAuthVo auth = result.get(0);
        Assert.assertEquals("u", auth.getUserUuid());
        Assert.assertEquals("BASE", auth.getAuth());
        Assert.assertEquals("process", auth.getAuthGroup());
        Assert.assertEquals(4, auth.getSourcePathList().size());
        JSONObject envelope = new JSONObject();
        envelope.put("userRoleAuthSourcePathList", result);
        JSONObject serialized = JSONObject.parseObject(envelope.toString(SerializerFeature.DisableCircularReferenceDetect));
        Assert.assertEquals(Arrays.asList(source("a", "A角色", null, null, null), source("z", "Z角色", null, null, null),
                source("r", "分组角色", "first", "银行/第一组", 2), source("r", "分组角色", "second", "银行/第二组", 4)), paths(serialized, "process", "BASE"));
        // 返回数组独立于输入记录，调用方后续清空返回来源也不会丢失原始授权来源。
        auth.getSourcePathList().clear();
        Assert.assertNotNull(roleList.get(0).getSourcePath());
        Assert.assertNull(roleList.get(0).getSourcePathList());
        Assert.assertNotNull(teamList.get(0).getSourcePath());
        Assert.assertNull(teamList.get(0).getSourcePathList());
    }

    /** 缺少角色名或所属分组路径时原始权限记录仍返回，但来源为空且不伪造最终说明。 */
    @Test
    public void shouldKeepPermissionsWithEmptySourcesWhenMetadataIsMissing() {
        RoleAuthVo missing = auth("missing", "process", "BASE");
        directAuths.addAll(Arrays.asList(missing, missing));
        TeamVo leaf = team("leaf", "叶分组", null, 2);
        userTeams.add(leaf);
        directTeamRoles.add(role("r", "分组角色", Collections.singletonList(leaf)));
        namedRoles.add(role("r", "分组角色", null));
        teamAuths.add(auth("r", "process", "BASE"));
        List<UserAuthVo> roleList = service.getRoleAuthListByUserUuid("u");
        List<UserAuthVo> teamList = service.getTeamAuthListByUserUuid("u");
        Assert.assertEquals(2, roleList.size());
        Assert.assertEquals(1, teamList.size());
        Assert.assertEquals("BASE", roleList.get(0).getAuth());
        Assert.assertEquals("u", teamList.get(0).getUserUuid());
        roleList.forEach(auth -> {
            Assert.assertNull(auth.getSourcePath());
            Assert.assertNull(auth.getSourcePathList());
        });
        Assert.assertNull(teamList.get(0).getSourcePath());
        Assert.assertNull(teamList.get(0).getSourcePathList());
        // 资料不足仍由入口完整查询，但最终不生成无可信来源的说明。
        Assert.assertTrue(service.getAuthSourcePathList("u").isEmpty());
        Assert.assertSame(missing, directAuths.get(0));
        Assert.assertEquals("missing", missing.getRoleUuid());
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

    /** 第二种汇总仍保留直接授权及重复来源，普通和包含结果只填聚合列表且与输入隔离。 */
    @Test
    public void shouldAggregateSingleSourcesInSecondMethodWithoutChangingItsSemantics() throws Exception {
        installGraph();
        UserAuthVo role = inheritedAuth("process", "INC_ROOT", sourceVo("r", "角色", null, null, null));
        UserAuthVo team = teamAuth("first", "t", "process", "INC_ROOT");
        List<UserAuthVo> roles = Collections.unmodifiableList(Arrays.asList(role, role));
        List<UserAuthVo> teams = Collections.singletonList(team);
        authQueriesForbidden = true;
        List<UserAuthVo> result = fixedSources(Collections.singletonList(userAuth("process", "INC_ROOT")), roles, teams)
                .getAuthSourcePathList2("u");
        for (UserAuthVo auth : result) {
            Assert.assertEquals("u", auth.getUserUuid());
            Assert.assertNull(auth.getSourcePath());
            Assert.assertNotNull(auth.getSourcePathList());
        }
        UserAuthVo root = result.stream().filter(row -> "INC_ROOT".equals(row.getAuth())).findFirst().get();
        Assert.assertEquals(3, root.getSourcePathList().size());
        Assert.assertEquals(Arrays.asList("r", "r", "t"), root.getSourcePathList().stream()
                .map(SourcePathVo::getRoleUuid).collect(java.util.stream.Collectors.toList()));
        UserAuthVo target = result.stream().filter(row -> "INC_TARGET".equals(row.getAuth())).findFirst().get();
        Assert.assertEquals("cmdb", target.getAuthGroup());
        Assert.assertEquals(4, target.getSourcePathList().size());
        Assert.assertNull(target.getSourcePathList().get(0).getRoleUuid());
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"),
                target.getSourcePathList().get(1).getIncludeAuthPathList().stream()
                        .map(node -> node.getString("auth")).collect(java.util.stream.Collectors.toList()));
        // 同一原始记录重复出现时也复制来源；修改结果不改变其他来源或输入路径。
        root.getSourcePathList().get(0).setRoleName("修改角色");
        Assert.assertEquals("角色", root.getSourcePathList().get(1).getRoleName());
        target.getSourcePathList().get(3).getTeamPathNameList().clear();
        target.getSourcePathList().get(1).getIncludeAuthPathList().get(0).put("auth", "CHANGED");
        Assert.assertEquals("角色", role.getSourcePath().getRoleName());
        Assert.assertNull(role.getSourcePath().getIncludeAuthPathList());
        Assert.assertEquals(Arrays.asList("银行", "第一组"), team.getSourcePath().getTeamPathNameList());
        Assert.assertEquals("INC_ROOT", target.getSourcePathList().get(2).getIncludeAuthPathList().get(0).getString("auth"));
        Assert.assertNull(role.getSourcePathList());
        Assert.assertNull(team.getSourcePathList());
        Assert.assertTrue(queryCounts.isEmpty());
    }

    /** 不再从旧聚合字段回退读取来源；空来源仍保留两种汇总各自既有的返回范围。 */
    @Test
    public void shouldReadOnlySingleSourceAndKeepMissingSourceBehavior() throws Exception {
        installGraph();
        UserAuthVo legacy = userAuth("process", "INC_ROOT");
        legacy.setSourcePathList(Collections.singletonList(sourceVo("r", "旧来源", null, null, null)));
        List<UserAuthVo> roles = Collections.singletonList(legacy);
        List<UserAuthVo> includes = service.getAuthIncludedAuthPathMap(null, roles, null);
        authQueriesForbidden = true;
        SourceInputService fixed = fixedSources(null, roles, null).withIncludedAuths(includes);
        Assert.assertTrue(fixed.getAuthSourcePathList("u").isEmpty());
        List<UserAuthVo> result = fixed.getAuthSourcePathList2("u");
        Assert.assertEquals(5, result.size());
        result.forEach(auth -> {
            Assert.assertNull(auth.getSourcePath());
            Assert.assertTrue(auth.getSourcePathList().isEmpty());
        });
        Assert.assertNull(legacy.getSourcePath());
        Assert.assertEquals("旧来源", legacy.getSourcePathList().get(0).getRoleName());
        Assert.assertTrue(queryCounts.isEmpty());
    }

    /** 独立来源DTO支持无参构造和完整属性；普通来源的JSON结构固定为六字段。 */
    @Test
    public void shouldRoundTripTypedSourcesThroughJsonAndJavaSerialization() throws Exception {
        SourcePathVo ordinary = new SourcePathVo();
        ordinary.setRoleUuid("r");
        ordinary.setRoleName("<管理员>角色");
        ordinary.setTeamUuid(null);
        ordinary.setTeamPathNameList(new ArrayList<>());
        ordinary.setTeamLft(null);
        ordinary.setIncludeAuthPathList(null);
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
        auth.setSourcePathList(new ArrayList<>(Arrays.asList(ordinary, included)));
        String json = JSONObject.toJSONString(auth, SerializerFeature.DisableCircularReferenceDetect);
        JSONObject wire = JSONObject.parseObject(json);
        JSONObject normal = wire.getJSONArray("sourcePathList").getJSONObject(0);
        Assert.assertEquals(new HashSet<>(Arrays.asList("roleUuid", "roleName", "teamUuid", "teamPathNameList", "teamLft", "includeAuthPathList")), normal.keySet());
        Assert.assertNull(normal.get("includeAuthPathList"));
        Assert.assertNull(normal.get("teamUuid"));
        Assert.assertNull(normal.get("teamLft"));
        UserAuthVo fromJson = JSONObject.parseObject(json, UserAuthVo.class);
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
            Assert.assertEquals(json, JSONObject.toJSONString(fromJava, SerializerFeature.DisableCircularReferenceDetect));
            Assert.assertEquals(SourcePathVo.class, fromJava.getSourcePathList().get(1).getClass());
        }
        Assert.assertNull(new UserAuthVo().getSourcePathList());
    }

    /** 可变DTO的来源对象、分组路径及链节点全部复制，修改返回值不影响输入或包含链缓存。 */
    @Test
    public void shouldIsolateMutableTypedSourcesAndIncludeNodes() throws Exception {
        installGraph();
        SourcePathVo input = sourceVo("r", "管理员", "leaf", "银行/叶分组", 2);
        UserAuthVo auth = inheritedAuth("process", "INC_ROOT", input);
        List<UserAuthVo> roleList = Collections.singletonList(auth);
        List<UserAuthVo> includes = service.getAuthIncludedAuthPathMap(null, roleList, null);
        List<JSONObject> originalChain = includedPath(includes, "INC_ROOT", "cmdb", "INC_TARGET");
        String snapshot = JSONObject.toJSONString(originalChain);
        authQueriesForbidden = true;
        List<UserAuthVo> result = fixedSources(null, roleList, null).withIncludedAuths(includes)
                .getAuthSourcePathList("u");
        SourcePathVo normal = result.stream().filter(row -> "INC_ROOT".equals(row.getAuth()))
                .findFirst().get().getSourcePathList().get(0);
        SourcePathVo included = result.stream().filter(row -> "INC_TARGET".equals(row.getAuth()))
                .findFirst().get().getSourcePathList().get(0);
        Assert.assertNotSame(input, normal);
        Assert.assertNotSame(input.getTeamPathNameList(), normal.getTeamPathNameList());
        Assert.assertNotSame(normal.getTeamPathNameList(), included.getTeamPathNameList());
        normal.setRoleName("已修改");
        normal.getTeamPathNameList().add("新增分组");
        included.getIncludeAuthPathList().get(0).put("auth", "CHANGED");
        Assert.assertEquals("管理员", input.getRoleName());
        Assert.assertEquals(Arrays.asList("银行", "叶分组"), input.getTeamPathNameList());
        Assert.assertEquals(Arrays.asList("银行", "叶分组"), included.getTeamPathNameList());
        Assert.assertEquals(snapshot, JSONObject.toJSONString(originalChain));
        Assert.assertNull(input.getIncludeAuthPathList());
        Assert.assertEquals(1, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
    }

    /** 包含来源由有效链而非子类决定，null、空链及单节点链沿用普通来源的身份去重。 */
    @Test
    public void shouldClassifyAndDeduplicateSourcesByValidIncludePath() {
        SourcePathVo ordinary = sourceVo("r", "管理员", null, null, null);
        SourcePathVo empty = sourceVo("r", "管理员", null, null, null);
        empty.setIncludeAuthPathList(new ArrayList<>());
        JSONObject root = new JSONObject();
        root.put("authGroup", "process");
        root.put("auth", "ROOT");
        JSONObject target = new JSONObject();
        target.put("authGroup", "process");
        target.put("auth", "TARGET");
        SourcePathVo single = sourceVo("r", "管理员", null, null, null);
        single.setIncludeAuthPathList(Collections.singletonList(root));
        SourcePathVo included = sourceVo("r", "管理员", null, null, null);
        included.setIncludeAuthPathList(Arrays.asList(root, target));
        authQueriesForbidden = true;
        List<UserAuthVo> result = fixedSources(null,
                Arrays.asList(inheritedAuth("process", "TARGET", ordinary), inheritedAuth("process", "TARGET", empty),
                        inheritedAuth("process", "TARGET", single), inheritedAuth("process", "TARGET", included)), null)
                .getAuthSourcePathList("u");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(2, result.get(0).getSourcePathList().size());
        Assert.assertNull(result.get(0).getSourcePathList().get(0).getIncludeAuthPathList());
        Assert.assertEquals(2, result.get(0).getSourcePathList().get(1).getIncludeAuthPathList().size());
        Assert.assertEquals("ROOT", result.get(0).getSourcePathList().get(1).getIncludeAuthPathList().get(0).getString("auth"));
    }

    /** 扁平列表中的无效记录不能匹配根身份，跳过后仍保留合法包含记录及原始输入。 */
    @Test
    public void shouldSkipIncludedRecordsWithoutValidChains() throws Exception {
        installGraph();
        List<UserAuthVo> roots = Collections.singletonList(userAuth("process", "INC_ROOT"));
        List<UserAuthVo> valid = service.getAuthIncludedAuthPathMap(roots, null, null);
        UserAuthVo target = valid.stream().filter(auth -> "INC_TARGET".equals(auth.getAuth())).findFirst().get();
        List<UserAuthVo> inputs = new ArrayList<>();
        inputs.add(null);
        inputs.add(userAuth("cmdb", "INC_TARGET"));
        inputs.add(inheritedAuth("cmdb", "INC_TARGET"));
        inputs.add(inheritedAuth("cmdb", "INC_TARGET", (SourcePathVo) null));
        inputs.add(inheritedAuth("cmdb", "INC_TARGET", new SourcePathVo()));
        JSONObject root = new JSONObject();
        root.put("authGroup", "process");
        root.put("auth", "INC_ROOT");
        for (List<JSONObject> chain : Arrays.asList(Collections.<JSONObject>emptyList(), Collections.singletonList(root),
                Arrays.asList(root, null), Arrays.asList(new JSONObject(), root))) {
            SourcePathVo source = new SourcePathVo();
            source.setIncludeAuthPathList(chain);
            inputs.add(inheritedAuth("cmdb", "INC_TARGET", source));
        }
        inputs.add(target);
        String before = inputSnapshot(inputs);
        authQueriesForbidden = true;
        List<UserAuthVo> result = fixedSources(roots, null, null).withIncludedAuths(Collections.unmodifiableList(inputs))
                .getAuthSourcePathList("u");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("INC_TARGET", result.get(0).getAuth());
        Assert.assertEquals("cmdb", result.get(0).getAuthGroup());
        Assert.assertEquals(1, result.get(0).getSourcePathList().size());
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"),
                result.get(0).getSourcePathList().get(0).getIncludeAuthPathList().stream()
                        .map(node -> node.getString("auth")).collect(java.util.stream.Collectors.toList()));
        Assert.assertEquals(before, inputSnapshot(inputs));
        Assert.assertTrue(queryCounts.isEmpty());
        Assert.assertEquals(1, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
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
        List<UserAuthVo> result = service.getTeamAuthListByUserUuid("u");
        Assert.assertEquals(Arrays.asList("first/r/process/BASE", "second/r/process/BASE", "first/r/cmdb/EDIT",
                "second/r/cmdb/EDIT", "first/r/process/BASE", "second/r/process/BASE"), teamAuthKeys(result));
        Assert.assertEquals(3, teamAuths.size());
        Assert.assertSame(original, teamAuths.get(0));
        Assert.assertSame(original, teamAuths.get(2));
        Assert.assertEquals("r", original.getRoleUuid());
        Assert.assertEquals("process", original.getAuthGroup());
        Assert.assertEquals("BASE", original.getAuth());
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchRoleAuthByRoleUuidList"));
    }

    /** 来源只相信每条分组授权的身份，同角色不同分组的根权限及直接角色来源不能串用。 */
    @Test
    public void shouldUseSuppliedTeamIdentityWithoutReloadingBindings() throws Exception {
        installGraph();
        namedRoles.add(role("r", "共享角色", null));
        detailedTeams.add(team("first", "第一组", "银行/第一组", 2));
        detailedTeams.add(team("second", "第二组", "银行/第二组", 4));
        List<UserAuthVo> inputs = Collections.unmodifiableList(Arrays.asList(
                teamAuth("first", "r", "process", "INC_ROOT"),
                teamAuth("second", "r", "process", "INC_SECOND_ROOT")));
        List<UserAuthVo> direct = Collections.singletonList(inheritedAuth("process", "INC_ROOT", sourceVo("r", "共享角色", null, null, null)));
        List<UserAuthVo> includePaths = service.getAuthIncludedAuthPathMap(null, direct, inputs);
        List<String> inputSnapshot = teamAuthKeys(inputs);
        authQueriesForbidden = true;
        JSONObject result = new JSONObject();
        result.put("userRoleAuthSourcePathList", fixedSources(null, direct, inputs).withIncludedAuths(includePaths)
                .getAuthSourcePathList("u"));
        JSONObject serialized = JSONObject.parseObject(result.toString(SerializerFeature.DisableCircularReferenceDetect));
        List<JSONObject> rootSources = paths(serialized, "process", "INC_ROOT");
        Assert.assertEquals(Arrays.asList(source("r", "共享角色", null, null, null),
                source("r", "共享角色", "first", "银行/第一组", 2)), rootSources);
        List<JSONObject> targets = paths(serialized, "cmdb", "INC_TARGET");
        Assert.assertEquals(3, targets.size());
        Assert.assertNull(targets.get(0).get("teamUuid"));
        Assert.assertEquals("first", targets.get(1).getString("teamUuid"));
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"), includeNames(targets.get(1)));
        Assert.assertEquals("second", targets.get(2).getString("teamUuid"));
        Assert.assertEquals(Arrays.asList("INC_SECOND_ROOT", "INC_TARGET"), includeNames(targets.get(2)));
        Assert.assertEquals(inputSnapshot, teamAuthKeys(inputs));
        Assert.assertEquals(1, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
        Assert.assertTrue(queryCounts.isEmpty());
    }

    /** 空白用户不访问数据库；空列表、null结果及空包含链均按空集合处理。 */
    @Test
    public void shouldHandleBlankUsersAndEmptyInputs() {
        for (String userUuid : Arrays.asList(null, "", "  ")) {
            Assert.assertTrue(service.getUserAuthListByUserUuid(userUuid).isEmpty());
            Assert.assertTrue(service.getRoleAuthListByUserUuid(userUuid).isEmpty());
            Assert.assertTrue(service.getTeamAuthListByUserUuid(userUuid).isEmpty());
            Assert.assertTrue(service.getAuthSourcePathList(userUuid).isEmpty());
            Assert.assertTrue(service.getAuthSourcePathList2(userUuid).isEmpty());
        }
        Assert.assertTrue(queryCounts.isEmpty());
        Assert.assertTrue(service.getAuthIncludedAuthPathMap(null, null, null).isEmpty());
        Assert.assertTrue(queryCounts.isEmpty());
        returnNullAuths = true;
        Assert.assertTrue(service.getUserAuthListByUserUuid("u").isEmpty());
        Assert.assertTrue(service.getRoleAuthListByUserUuid("u").isEmpty());
        Assert.assertTrue(service.getTeamAuthListByUserUuid("u").isEmpty());
        Assert.assertFalse(queryCounts.containsKey("searchRoleAuthByRoleUuidList"));
        TeamVo leaf = team("leaf", "叶分组", "银行/叶分组", 2);
        userTeams.add(leaf);
        directTeamRoles.add(role("r", "管理员", Collections.singletonList(leaf)));
        Assert.assertTrue(service.getTeamAuthListByUserUuid("u").isEmpty());
        Assert.assertTrue(service.getAuthSourcePathList("u").isEmpty());
    }

    /** 包含链按根编码排序，三类重复根只遍历一次，跨组目标使用自身分组且不改变输入。 */
    @Test
    public void shouldCacheStableIncludePathsWithoutDatabaseQueriesOrInputChanges() throws Exception {
        installGraph();
        userAuths.add(userAuth("process", "INC_SECOND_ROOT"));
        userAuths.add(userAuth("process", "INC_ROOT"));
        directAuths.add(auth("r", "process", "INC_ROOT"));
        teamAuths.add(auth("r", "process", "INC_ROOT"));
        List<UserAuthVo> includePaths = service.getAuthIncludedAuthPathMap(userAuths, Collections.singletonList(userAuth("process", "INC_ROOT")),
                Collections.singletonList(teamAuth("leaf", "r", "process", "INC_ROOT")));
        // 不同根的相同目标分别保留，列表顺序为根编码升序及根内广度优先顺序。
        Assert.assertEquals(Arrays.asList("INC_ROOT/process/INC_LEFT", "INC_ROOT/process/INC_RIGHT",
                "INC_ROOT/cmdb/INC_TARGET", "INC_ROOT/autoexec/INC_DEEP",
                "INC_SECOND_ROOT/cmdb/INC_TARGET", "INC_SECOND_ROOT/autoexec/INC_DEEP"),
                includePaths.stream().map(auth -> auth.getSourcePath().getIncludeAuthPathList().get(0).getString("auth")
                        + "/" + auth.getAuthGroup() + "/" + auth.getAuth()).collect(java.util.stream.Collectors.toList()));
        Assert.assertEquals(2, includePaths.stream().filter(auth -> "INC_TARGET".equals(auth.getAuth())).count());
        for (UserAuthVo auth : includePaths) {
            Assert.assertNull(auth.getUserUuid());
            Assert.assertNull(auth.getSourcePathList());
            SourcePathVo source = auth.getSourcePath();
            Assert.assertNotNull(source);
            Assert.assertNull(source.getRoleUuid());
            Assert.assertNull(source.getRoleName());
            Assert.assertNull(source.getTeamUuid());
            Assert.assertNull(source.getTeamLft());
            Assert.assertEquals(Collections.emptyList(), source.getTeamPathNameList());
            List<JSONObject> chain = source.getIncludeAuthPathList();
            Assert.assertEquals(auth.getAuth(), chain.get(chain.size() - 1).getString("auth"));
            Assert.assertEquals(auth.getAuthGroup(), chain.get(chain.size() - 1).getString("authGroup"));
        }
        Assert.assertEquals(1, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
        List<JSONObject> targetPath = includedPath(includePaths, "INC_ROOT", "cmdb", "INC_TARGET");
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"), targetPath.stream().map(row -> row.getString("auth")).collect(java.util.stream.Collectors.toList()));
        Assert.assertEquals("cmdb", targetPath.get(2).getString("authGroup"));
        Assert.assertTrue(queryCounts.isEmpty());
        Assert.assertEquals("INC_SECOND_ROOT", userAuths.get(0).getAuth());
        Assert.assertEquals("INC_ROOT", userAuths.get(1).getAuth());
        Assert.assertEquals("r", directAuths.get(0).getRoleUuid());
        Assert.assertEquals("r", teamAuths.get(0).getRoleUuid());
    }

    /** 固定输入隔离聚合边界；入口复用本次计算结果，来源与包含链缓存均不被修改。 */
    @Test
    public void shouldBuildSourcesWithoutMutatingInputsOrRecomputingPaths() throws Exception {
        installGraph();
        namedRoles.add(role("r", "管理员", null));
        userAuths.add(userAuth("process", "INC_ROOT"));
        directAuths.add(auth("r", "process", "INC_ROOT"));
        List<UserAuthVo> roleList = service.getRoleAuthListByUserUuid("u");
        String sourceSnapshot = JSONObject.toJSONString(roleList.get(0).getSourcePath());
        List<UserAuthVo> includePaths = service.getAuthIncludedAuthPathMap(userAuths, roleList, Collections.emptyList());
        List<JSONObject> targetPath = includedPath(includePaths, "INC_ROOT", "cmdb", "INC_TARGET");
        String snapshot = JSONObject.toJSONString(targetPath);
        authQueriesForbidden = true;
        SourceInputService fixed = fixedSources(Collections.unmodifiableList(userAuths),
                Collections.unmodifiableList(roleList), Collections.emptyList()).withIncludedAuths(Collections.unmodifiableList(includePaths));
        List<UserAuthVo> sources = fixed.getAuthSourcePathList("u");
        Assert.assertEquals(1, fixed.includeCalls);
        Assert.assertEquals(1, ((INC_ROOT) registry.get("INC_ROOT")).includeReadCount);
        Assert.assertSame(targetPath, includedPath(includePaths, "INC_ROOT", "cmdb", "INC_TARGET"));
        Assert.assertEquals(snapshot, JSONObject.toJSONString(targetPath));
        Assert.assertEquals(1, userAuths.size());
        Assert.assertEquals(1, directAuths.size());
        Assert.assertEquals(sourceSnapshot, JSONObject.toJSONString(roleList.get(0).getSourcePath()));
        for (UserAuthVo auth : sources) {
            Assert.assertEquals("u", auth.getUserUuid());
            Assert.assertNotNull(auth.getSourcePathList());
            Assert.assertNull(auth.getSourcePath());
        }
        JSONObject envelope = new JSONObject();
        envelope.put("userRoleAuthSourcePathList", sources);
        JSONObject serialized = JSONObject.parseObject(envelope.toString(SerializerFeature.DisableCircularReferenceDetect));
        List<JSONObject> rows = paths(serialized, "cmdb", "INC_TARGET");
        Assert.assertEquals(2, rows.size());
        Assert.assertNull(rows.get(0).get("roleUuid"));
        Assert.assertTrue(rows.get(0).containsKey("roleUuid"));
        Assert.assertTrue(rows.get(0).containsKey("teamUuid"));
        Assert.assertTrue(rows.get(0).containsKey("teamLft"));
        Assert.assertEquals(new JSONArray(), rows.get(0).getJSONArray("teamPathNameList"));
        Assert.assertEquals("r", rows.get(1).getString("roleUuid"));
        Assert.assertNull(paths(serialized, "process", "INC_ROOT").get(0).get("includeAuthPathList"));
        Assert.assertEquals(1, fixedSources(userAuths, roleList, Collections.emptyList()).withIncludedAuths(null)
                .getAuthSourcePathList("u").size());
    }

    /** 重复角色授权保留全部来源，直接角色优先，同类排序且不同权限组相互独立。 */
    @Test
    public void shouldMergeDirectAndTeamSourcesWithCompatibleStructure() throws Exception {
        namedRoles.add(role("z", "管理员", null));
        namedRoles.add(role("a", "值班角色", null));
        directAuths.add(auth("z", "process", "BASE"));
        directAuths.add(auth("a", "process", "BASE"));
        directAuths.add(auth("z", "process", "BASE"));
        directAuths.add(auth("a", "cmdb", "BASE"));
        TeamVo root = team("root", "苏州银行", "苏州银行", 1);
        TeamVo leaf = team("leaf", "金融同业部", "苏州银行/金融市场总部/金融同业部", 3);
        userTeams.addAll(Arrays.asList(root, leaf));
        detailedTeams.addAll(userTeams);
        directTeamRoles.add(role("z", "管理员", Arrays.asList(root, leaf)));
        ancestorRoles.put("leaf", Collections.singletonList(role("z", "管理员", Collections.singletonList(root))));
        teamAuths.add(auth("z", "process", "BASE"));
        teamAuths.add(auth("z", "process", "BASE"));
        userAuths.add(userAuth("process", "BASE"));
        userAuths.add(userAuth("process", "EDIT"));
        JSONObject result = search();
        Assert.assertEquals(Arrays.asList(source("a", "值班角色", null, null, null),
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

    /** 缺失角色名、分组路径或完整祖先链不拼造来源，旧权限照常回显。 */
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

    /** 没有角色或包含授权时返回空来源列表。 */
    @Test
    public void shouldReturnEmptySourcesWithoutRoles() throws Exception {
        userAuths.add(userAuth("process", "EDIT"));
        JSONObject result = search();
        Assert.assertEquals(new JSONArray(), result.getJSONArray("userRoleAuthSourcePathList"));
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

    /** 直接角色同名不同UUID全部保留，分组顺序按lft而非路径文字，相同lft按UUID排序。 */
    @Test
    public void shouldSortByTreeOrderAndKeepDistinctIdentities() throws Exception {
        namedRoles.add(role("z", "相同角色", null));
        namedRoles.add(role("a", "相同角色", null));
        directAuths.add(auth("z", "process", "BASE"));
        directAuths.add(auth("a", "process", "BASE"));
        directAuths.add(auth("z", "process", "BASE"));
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
        teamAuths.add(auth("z", "process", "BASE"));
        List<JSONObject> rows = paths(search(), "process", "BASE");
        Assert.assertEquals(Arrays.asList(source("a", "相同角色", null, null, null),
                source("z", "相同角色", null, null, null), source("z", "相同角色", "z-early", "Z分组", 1),
                source("a", "相同角色", "a-late", "A分组", 2), source("z", "相同角色", "a-late", "A分组", 2),
                source("z", "相同角色", "b-late", "B分组", 2), source("z", "相同角色", "a-null", "C分组", null),
                source("z", "相同角色", "b-null", "D分组", null)), rows);
        Assert.assertTrue(rows.get(0).containsKey("teamUuid"));
        Assert.assertTrue(rows.get(0).containsKey("teamLft"));
        Assert.assertTrue(rows.get(6).containsKey("teamLft"));
        Assert.assertNull(rows.get(6).get("teamLft"));
    }

    /** 同一分组内先按原始角色名再按UUID排序，不受Mapper返回顺序影响。 */
    @Test
    public void shouldSortRolesWithinTeamByRawNameThenUuid() throws Exception {
        TeamVo team = team("t", "银行", "银行", 1);
        userTeams.add(team);
        detailedTeams.add(team);
        for (String uuid : Arrays.asList("z", "m", "a")) {
            String name = "m".equals(uuid) ? "A管理员" : "B管理员";
            namedRoles.add(role(uuid, name, null));
            directTeamRoles.add(role(uuid, name, Collections.singletonList(team)));
            teamAuths.add(auth(uuid, "process", "BASE"));
        }
        Assert.assertEquals(Arrays.asList(source("m", "A管理员", "t", "银行", 1),
                source("a", "B管理员", "t", "银行", 1), source("z", "B管理员", "t", "银行", 1)),
                paths(search(), "process", "BASE"));
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
        Assert.assertTrue(included.containsKey("roleUuid"));
        Assert.assertNull(included.get("roleUuid"));
        Assert.assertTrue(included.containsKey("teamUuid"));
        Assert.assertNull(included.get("teamUuid"));
        Assert.assertEquals(2, paths(result, "autoexec", "INC_DEEP").size());
    }

    /** 角色旧来源保持五字段并优先，包含来源依次为用户直接、直接角色和所属分组。 */
    @Test
    public void shouldKeepLegacySourcesBeforeIncludedRoleAndTeamSources() throws Exception {
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
        JSONObject result = search();
        List<JSONObject> rows = paths(result, "cmdb", "INC_TARGET");
        Assert.assertEquals(4, rows.size());
        Assert.assertTrue(rows.get(0).containsKey("includeAuthPathList"));
        Assert.assertNull(rows.get(0).get("includeAuthPathList"));
        Assert.assertEquals(source("legacy", "目标角色", null, null, null), rows.get(0));
        Assert.assertNull(rows.get(1).get("roleUuid"));
        Assert.assertEquals("r", rows.get(2).getString("roleUuid"));
        Assert.assertNull(rows.get(2).get("teamUuid"));
        Assert.assertEquals("leaf", rows.get(3).getString("teamUuid"));
        Assert.assertEquals(Arrays.asList("银行", "叶分组"), rows.get(3).getJSONArray("teamPathNameList"));
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"), includeNames(rows.get(3)));
    }

    /** 来源资料缺失时仍展开只读对象，不借用其他角色或分组伪造说明。 */
    @Test
    public void shouldExpandIncludesEvenWhenRoleMetadataIsMissing() throws Exception {
        installGraph();
        directAuths.add(auth("missing", "process", "INC_ROOT"));
        JSONObject result = search();
        Assert.assertTrue(result.getJSONArray("userRoleAuthSourcePathList").isEmpty());
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"),
                includedPath(service.getAuthIncludedAuthPathMap(userAuths, service.getRoleAuthListByUserUuid("u"), Collections.emptyList()),
                        "INC_ROOT", "cmdb", "INC_TARGET").stream().map(node -> node.getString("auth"))
                        .collect(java.util.stream.Collectors.toList()));
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
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"), includeNames(rows.get(0)));
        Assert.assertEquals(Arrays.asList("INC_SHORT_ROOT", "INC_TARGET"), includeNames(rows.get(1)));
        // ROOT只会因为SHORT_ROOT包含它而只读，不允许ROOT自己产生ROOT->ROOT来源。
        List<JSONObject> rootRows = paths(result, "process", "INC_ROOT");
        Assert.assertEquals(1, rootRows.size());
        Assert.assertEquals(Arrays.asList("INC_SHORT_ROOT", "INC_ROOT"), includeNames(rootRows.get(0)));
    }

    /** 同一来源从多个数据库根获得目标时分别保留，重复的同根数据库记录只出现一次。 */
    @Test
    public void shouldKeepMultipleRootsAndDeduplicateRepeatedDatabaseRows() throws Exception {
        installGraph();
        namedRoles.add(role("r", "管理员", null));
        for (String root : Arrays.asList("INC_SECOND_ROOT", "INC_ROOT", "INC_ROOT")) {
            directAuths.add(auth("r", "process", root));
        }
        List<JSONObject> rows = paths(search(), "cmdb", "INC_TARGET");
        Assert.assertEquals(2, rows.size());
        Assert.assertEquals(Arrays.asList("INC_ROOT", "INC_LEFT", "INC_TARGET"), includeNames(rows.get(0)));
        Assert.assertEquals(Arrays.asList("INC_SECOND_ROOT", "INC_TARGET"), includeNames(rows.get(1)));
    }

    /** 两个单参数入口在有效且无重复来源时内容相同，并各自只查询一次三类原始授权。 */
    @Test
    public void shouldMatchBothImplementationsForDistinctValidSources() throws Exception {
        installGraph();
        namedRoles.addAll(Arrays.asList(role("r", "直接角色", null), role("s", "第二角色", null), role("t", "分组角色", null)));
        directAuths.addAll(Arrays.asList(auth("r", "process", "INC_ROOT"), auth("s", "process", "INC_SECOND_ROOT"),
                auth("r", "process", "BASE"), auth("r", "cmdb", "BASE")));
        TeamVo first = team("first", "第一组", "银行/第一组", 2);
        TeamVo second = team("second", "第二组", "银行/第二组", 4);
        userTeams.addAll(Arrays.asList(first, second));
        detailedTeams.addAll(userTeams);
        directTeamRoles.add(role("t", "分组角色", userTeams));
        teamAuths.add(auth("t", "process", "INC_ROOT"));
        List<UserAuthVo> original = service.getAuthSourcePathList("u");
        assertAuthorizationQueriesOnce();
        Assert.assertEquals(Integer.valueOf(2), queryCounts.get("getRoleByUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("getTeamByUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchRoleAuthByRoleUuidList"));
        queryCounts.clear();
        List<UserAuthVo> alternative = service.getAuthSourcePathList2("u");
        assertAuthorizationQueriesOnce();
        Assert.assertEquals(Integer.valueOf(2), queryCounts.get("getRoleByUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("getTeamByUuidList"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchRoleAuthByRoleUuidList"));
        String before = JSONObject.toJSONString(original);
        Assert.assertEquals(normalizedResults(original), normalizedResults(alternative));
        Assert.assertEquals(before, JSONObject.toJSONString(original));
        Assert.assertEquals(4, findAuth(original, "cmdb", "INC_TARGET").getSourcePathList().size());
        Assert.assertNotNull(findAuth(alternative, "process", "BASE"));
        Assert.assertNotNull(findAuth(alternative, "cmdb", "BASE"));
    }

    /** 直接授权只应为包含项提供来源；新实现目前额外返回空来源的根权限。 */
    @Test
    public void shouldDocumentExtraDirectPermissionsInAlternative() throws Exception {
        installGraph();
        for (String root : Arrays.asList("INC_DEEP", "INC_ROOT")) {
            userAuths.clear();
            userAuths.add(userAuth("INC_DEEP".equals(root) ? "autoexec" : "process", root));
            List<UserAuthVo> original = service.getAuthSourcePathList("u");
            List<UserAuthVo> alternative = service.getAuthSourcePathList2("u");
            Assert.assertEquals("INC_DEEP".equals(root) ? 0 : 4, original.size());
            Assert.assertEquals(original.size() + 1, alternative.size());
            Assert.assertTrue(findAuth(alternative, userAuths.get(0).getAuthGroup(), root).getSourcePathList().isEmpty());
            // 仅去掉明确的额外根记录后，目标权限及完整包含来源应相同。
            List<UserAuthVo> targets = new ArrayList<>(alternative);
            targets.removeIf(auth -> root.equals(auth.getAuth()));
            Assert.assertEquals(normalizedResults(original), normalizedResults(targets));
        }
    }

    /** 真实查询保留缺失资料的权限记录；新实现会将这些记录及其包含项作为空来源结果返回。 */
    @Test
    public void shouldDocumentEmptySourceRowsFromMissingMetadata() throws Exception {
        installGraph();
        directAuths.add(auth("missing", "process", "INC_ROOT"));
        TeamVo leaf = team("leaf", "叶分组", null, 2);
        userTeams.add(leaf);
        namedRoles.add(role("t", "分组角色", null));
        directTeamRoles.add(role("t", "分组角色", Collections.singletonList(leaf)));
        teamAuths.add(auth("t", "process", "INC_ROOT"));
        Assert.assertTrue(service.getAuthSourcePathList("u").isEmpty());
        List<UserAuthVo> alternative = service.getAuthSourcePathList2("u");
        Assert.assertEquals(5, alternative.size());
        alternative.forEach(auth -> Assert.assertTrue(auth.getSourcePathList().isEmpty()));
    }

    /** 重复用户根、角色和分组记录在新实现中重复追加，规范化比较也必须保留重复项。 */
    @Test
    public void shouldDocumentDuplicateSourcesForEachOrigin() throws Exception {
        installGraph();
        for (int origin = 0; origin < 3; origin++) {
            UserAuthVo root = origin == 0 ? userAuth("process", "INC_ROOT")
                    : origin == 1 ? inheritedAuth("process", "INC_ROOT", sourceVo("r", "角色", null, null, null))
                    : teamAuth("first", "r", "process", "INC_ROOT");
            List<UserAuthVo> repeated = Collections.unmodifiableList(Arrays.asList(root, root));
            SourceInputService fixed = fixedSources(origin == 0 ? repeated : null,
                    origin == 1 ? repeated : null, origin == 2 ? repeated : null);
            String before = inputSnapshot(repeated);
            List<UserAuthVo> original = fixed.getAuthSourcePathList("u");
            List<UserAuthVo> alternative = fixed.getAuthSourcePathList2("u");
            Assert.assertEquals(1, findAuth(original, "cmdb", "INC_TARGET").getSourcePathList().size());
            Assert.assertEquals(2, findAuth(alternative, "cmdb", "INC_TARGET").getSourcePathList().size());
            Assert.assertNotEquals(normalizedResults(original), normalizedResults(alternative));
            Assert.assertEquals(before, inputSnapshot(repeated));
            if (origin != 0) {
                Assert.assertEquals(1, findAuth(original, "process", "INC_ROOT").getSourcePathList().size());
                Assert.assertEquals(2, findAuth(alternative, "process", "INC_ROOT").getSourcePathList().size());
            }
        }
    }

    /** 非null来源仍可能缺少角色身份或完整分组路径；新实现目前不会执行原有可信来源校验。 */
    @Test
    public void shouldDocumentInvalidNonNullSourcesInAlternative() throws Exception {
        installGraph();
        for (SourcePathVo invalid : Arrays.asList(sourceVo(null, "角色", null, null, null),
                sourceVo("r", "  ", null, null, null), sourceVo("r", "角色", "leaf", null, 2),
                sourceVo("r", "角色", "leaf", "银行/", 2))) {
            UserAuthVo root = inheritedAuth("process", "INC_ROOT", invalid);
            SourceInputService fixed = fixedSources(null, invalid.getTeamUuid() == null ? Collections.singletonList(root) : null,
                    invalid.getTeamUuid() != null ? Collections.singletonList(root) : null);
            Assert.assertTrue(fixed.getAuthSourcePathList("u").isEmpty());
            List<UserAuthVo> alternative = fixed.getAuthSourcePathList2("u");
            Assert.assertEquals(5, alternative.size());
            Assert.assertEquals(JSONObject.toJSONString(invalid), JSONObject.toJSONString(
                    findAuth(alternative, "process", "INC_ROOT").getSourcePathList().get(0)));
        }
    }

    /** 空白组或编码被原聚合过滤；null编码另会在原根排序阶段抛异常，不能混同于空白过滤。 */
    @Test
    public void shouldDocumentBlankIdentifiersAndNullAuthBehavior() {
        for (UserAuthVo invalid : Arrays.asList(inheritedAuth(null, "UNREGISTERED", sourceVo("r", "角色", null, null, null)),
                inheritedAuth("  ", "UNREGISTERED", sourceVo("r", "角色", null, null, null)),
                inheritedAuth("process", "", sourceVo("r", "角色", null, null, null)),
                inheritedAuth("process", "  ", sourceVo("r", "角色", null, null, null)))) {
            SourceInputService fixed = fixedSources(null, Collections.singletonList(invalid), null);
            Assert.assertTrue(fixed.getAuthSourcePathList("u").isEmpty());
            List<UserAuthVo> alternative = fixed.getAuthSourcePathList2("u");
            Assert.assertEquals(1, alternative.size());
            Assert.assertEquals(invalid.getAuth(), alternative.get(0).getAuth());
            Assert.assertEquals(invalid.getAuthGroup(), alternative.get(0).getAuthGroup());
        }
        SourceInputService fixed = fixedSources(null,
                Collections.singletonList(inheritedAuth("process", null, sourceVo("r", "角色", null, null, null))), null);
        try {
            fixed.getAuthSourcePathList("u");
            Assert.fail("原实现的TreeSet根编码排序不接受null");
        } catch (NullPointerException expected) {
            Assert.assertEquals(1, fixed.getAuthSourcePathList2("u").size());
        }
    }

    /** 单独比较遍历次数，避免将重复计算与返回内容差异混为一谈。 */
    @Test
    public void shouldCompareRootExpansionCountsSeparatelyFromResults() throws Exception {
        installGraph();
        SourceInputService fixed = fixedSources(Collections.singletonList(userAuth("process", "INC_ROOT")),
                Collections.singletonList(inheritedAuth("process", "INC_ROOT", sourceVo("r", "角色", null, null, null))),
                Collections.singletonList(teamAuth("first", "r", "process", "INC_ROOT")));
        INC_ROOT root = (INC_ROOT) registry.get("INC_ROOT");
        List<UserAuthVo> original = fixed.getAuthSourcePathList("u");
        Assert.assertEquals(1, root.includeReadCount);
        Assert.assertEquals(1, fixed.includeCalls);
        root.includeReadCount = 0;
        List<UserAuthVo> alternative = fixed.getAuthSourcePathList2("u");
        Assert.assertEquals(3, root.includeReadCount);
        Assert.assertEquals(1, fixed.includeCalls);
        Assert.assertEquals(normalizedResults(original), normalizedResults(alternative));
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

    /** 构造预期HTTP结构，显式保留null键并校验字段集合。 */
    private JSONObject source(String roleUuid, String roleName, String teamUuid, String path, Integer lft) {
        JSONObject row = new JSONObject();
        row.put("roleUuid", roleUuid);
        row.put("roleName", roleName);
        row.put("teamUuid", teamUuid);
        row.put("teamPathNameList", path == null ? Collections.emptyList() : Arrays.asList(path.split("/", -1)));
        row.put("teamLft", lft);
        row.put("includeAuthPathList", null);
        return row;
    }

    /** 按包含链根节点及目标权限组、编码定位，返回原链引用以验证缓存和输入未被修改。 */
    private List<JSONObject> includedPath(List<UserAuthVo> authList, String root, String group, String auth) {
        for (UserAuthVo row : authList) {
            if (group.equals(row.getAuthGroup()) && auth.equals(row.getAuth())) {
                SourcePathVo source = row.getSourcePath();
                List<JSONObject> chain = source.getIncludeAuthPathList();
                if (root.equals(chain.get(0).getString("auth"))) {
                    return chain;
                }
            }
        }
        Assert.fail("缺少包含链：" + root + " -> " + group + "/" + auth);
        return Collections.emptyList();
    }

    /** 固定输入子类仅用于聚合边界，生产方法签名与内部实现均保持不变。 */
    private SourceInputService fixedSources(List<UserAuthVo> users, List<UserAuthVo> roles, List<UserAuthVo> teams) {
        return new SourceInputService(users, roles, teams);
    }

    /** 通过公开查询方法提供只读输入，必要时替换本次包含计算结果以覆盖无效链和复制边界。 */
    private static class SourceInputService extends UserServiceImpl {
        private final List<UserAuthVo> users;
        private final List<UserAuthVo> roles;
        private final List<UserAuthVo> teams;
        private boolean suppliedIncludes;
        private List<UserAuthVo> includes;
        private int includeCalls;

        private SourceInputService(List<UserAuthVo> users, List<UserAuthVo> roles, List<UserAuthVo> teams) {
            this.users = users;
            this.roles = roles;
            this.teams = teams;
        }

        /** 显式区分未替换计算和替换为null的异常边界，不访问Mapper或改写生产代码。 */
        private SourceInputService withIncludedAuths(List<UserAuthVo> includes) {
            this.suppliedIncludes = true;
            this.includes = includes;
            return this;
        }

        /** 直接授权由固定输入提供，保留调用方原始列表。 */
        @Override
        public List<UserAuthVo> getUserAuthListByUserUuid(String userUuid) {
            return users;
        }

        /** 角色授权由固定输入提供，保留单来源身份。 */
        @Override
        public List<UserAuthVo> getRoleAuthListByUserUuid(String userUuid) {
            return roles;
        }

        /** 分组授权由固定输入提供，不重新查询角色和所属分组关联。 */
        @Override
        public List<UserAuthVo> getTeamAuthListByUserUuid(String userUuid) {
            return teams;
        }

        /** 默认执行真实包含算法；只有边界测试显式替换包含结果。 */
        @Override
        public List<UserAuthVo> getAuthIncludedAuthPathMap(List<UserAuthVo> users, List<UserAuthVo> roles, List<UserAuthVo> teams) {
            includeCalls++;
            return suppliedIncludes ? includes : super.getAuthIncludedAuthPathMap(users, roles, teams);
        }
    }

    /** 三类原始授权各查询一次；Mapper代理的白名单同时禁止任何写入或额外查询。 */
    private void assertAuthorizationQueriesOnce() {
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchUserAuthByUserUuid"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("searchUserRoleAuthByUserUuid"));
        Assert.assertEquals(Integer.valueOf(1), queryCounts.get("getTeamListByUserUuid"));
    }

    /** 仅在副本上排序两层列表，保留重复项、null字段及完整链，避免规范化掩盖实际差异。 */
    private List<String> normalizedResults(List<UserAuthVo> auths) {
        List<String> rows = new ArrayList<>();
        for (UserAuthVo auth : auths) {
            Assert.assertNull(auth.getSourcePath());
            Assert.assertNotNull(auth.getSourcePathList());
            JSONObject row = JSONObject.parseObject(JSONObject.toJSONString(auth,
                    SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
            List<String> paths = new ArrayList<>();
            for (SourcePathVo source : auth.getSourcePathList()) {
                paths.add(JSONObject.toJSONString(source, SerializerFeature.MapSortField,
                        SerializerFeature.WriteMapNullValue, SerializerFeature.DisableCircularReferenceDetect));
            }
            Collections.sort(paths);
            row.put("sourcePathList", paths);
            rows.add(JSONObject.toJSONString(row, SerializerFeature.MapSortField, SerializerFeature.WriteMapNullValue));
        }
        Collections.sort(rows);
        return rows;
    }

    /** 单来源被DTO的JSON配置隐藏，输入快照需显式包含该字段才能发现链和路径被修改。 */
    private String inputSnapshot(List<UserAuthVo> auths) {
        List<JSONObject> rows = new ArrayList<>();
        for (UserAuthVo auth : auths) {
            if (auth == null) {
                rows.add(null);
                continue;
            }
            JSONObject row = new JSONObject();
            row.put("userUuid", auth.getUserUuid());
            row.put("authGroup", auth.getAuthGroup());
            row.put("auth", auth.getAuth());
            row.put("sourcePath", auth.getSourcePath());
            row.put("sourcePathList", auth.getSourcePathList());
            rows.add(row);
        }
        return JSONObject.toJSONString(rows, SerializerFeature.WriteMapNullValue, SerializerFeature.DisableCircularReferenceDetect);
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
        envelope.put("userRoleAuthSourcePathList", service.getAuthSourcePathList(userUuid));
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

    /** 固定测试中的完整分组来源，供纯内存包含链及来源测试使用。 */
    private UserAuthVo teamAuth(String teamUuid, String roleUuid, String group, String name) {
        String teamName = "first".equals(teamUuid) ? "第一组" : "第二组";
        return inheritedAuth(group, name, sourceVo(roleUuid, "共享角色", teamUuid, "银行/" + teamName, "first".equals(teamUuid) ? 2 : 4));
    }

    /** 从记录自带的来源读取身份，验证原始授权及分组展开的返回顺序。 */
    private List<String> teamAuthKeys(List<UserAuthVo> auths) {
        List<String> keys = new ArrayList<>();
        for (UserAuthVo auth : auths) {
            Assert.assertNull(auth.getSourcePathList());
            SourcePathVo path = auth.getSourcePath();
            Assert.assertNotNull(path);
            keys.add(path.getTeamUuid() + "/" + path.getRoleUuid() + "/" + auth.getAuthGroup() + "/" + auth.getAuth());
        }
        return keys;
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
