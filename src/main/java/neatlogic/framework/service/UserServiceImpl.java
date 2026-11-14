/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.service;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.auth.core.AuthFactory;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.systemuser.SystemUserFactory;
import neatlogic.framework.dao.mapper.*;
import neatlogic.framework.dto.RoleAuthVo;
import neatlogic.framework.dto.SourcePathVo;
import neatlogic.framework.dto.RoleVo;
import neatlogic.framework.dto.UserAuthVo;
import neatlogic.framework.dto.RoleTeamVo;
import neatlogic.framework.dto.RoleUserVo;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.user.UserNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private RoleMapper roleMapper;

    @Override
    public List<String> getUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidList(List<String> userUuidList, List<String> teamUuidList, List<String> roleUuidList) {
        Set<String> resultSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(userUuidList)) {
            resultSet.addAll(userUuidList);
        }
        if (CollectionUtils.isNotEmpty(teamUuidList)) {
            List<String> list = userMapper.getUserUuidListByTeamUuidList(teamUuidList);
            if (CollectionUtils.isNotEmpty(list)) {
                resultSet.addAll(list);
            }
        }
        if (CollectionUtils.isNotEmpty(roleUuidList)) {
            Set<String> userUuidSet = new HashSet<>();
            {
                List<String> list = userMapper.getUserUuidListByRoleUuidList(roleUuidList);
                userUuidSet.addAll(list);
            }
            Set<String> teamUuidSet = new HashSet<>();
            List<RoleTeamVo> roleTeamList = roleMapper.getRoleTeamListByRoleUuidList(roleUuidList);
            for (RoleTeamVo roleTeamVo : roleTeamList) {
                String teamUuid = roleTeamVo.getTeamUuid();
                if (Objects.equals(roleTeamVo.getCheckedChildren(), 1)) {
                    TeamVo teamVo = teamMapper.getTeamByUuid(teamUuid);
                    if (teamVo != null) {
                        teamUuidSet.add(teamUuid);
                        List<String> list = teamMapper.getChildrenUuidListByLeftRightCode(teamVo.getLft(), teamVo.getRht());
                        teamUuidSet.addAll(list);
                    }
                } else {
                    teamUuidSet.add(teamUuid);
                }
                if (teamUuidSet.size() >= 100) {
                    userUuidList = userMapper.getUserUuidListByTeamUuidList(new ArrayList<>(teamUuidSet));
                    userUuidSet.addAll(userUuidList);
                    teamUuidSet.clear();
                }
            }
            if (CollectionUtils.isNotEmpty(teamUuidSet)) {
                userUuidList = userMapper.getUserUuidListByTeamUuidList(new ArrayList<>(teamUuidSet));
                userUuidSet.addAll(userUuidList);
            }
            if (CollectionUtils.isNotEmpty(userUuidSet)) {
                resultSet.addAll(userUuidSet);
            }
        }
        if (CollectionUtils.isNotEmpty(resultSet)) {
            return userMapper.getUserUuidListByUuidListAndIsActive(new ArrayList<>(resultSet), 1);
        }
        return new ArrayList<>();
    }

    /**
     * @Description: 根据用户uuid集合与分组uuid集合查询激活的用户uuid
     * @Author: laiwt
     * @Date: 2021/3/5 17:06
     * @Params: [userUuidList, teamUuidList]
     * @Returns: java.util.Set<java.lang.String>
     **/
    @Override
    public Set<String> getUserUuidSetByUserUuidListAndTeamUuidList(List<String> userUuidList, List<String> teamUuidList) {
//        Set<String> uuidList = new HashSet<>();
//        if (CollectionUtils.isNotEmpty(userUuidList)) {
//            List<String> existUserUuidList = userMapper.getUserUuidListByUuidListAndIsActive(userUuidList, 1);
//            if (CollectionUtils.isNotEmpty(existUserUuidList)) {
//                uuidList.addAll(new HashSet<>(existUserUuidList));
//            }
//        }
//        if (CollectionUtils.isNotEmpty(teamUuidList)) {
//            List<String> list = userMapper.getUserUuidListByTeamUuidList(teamUuidList);
//            if (CollectionUtils.isNotEmpty(list)) {
//                uuidList.addAll(new HashSet<>(list));
//            }
//        }
        List<String> uuidList = getUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidList(userUuidList, teamUuidList, null);
        return new HashSet<>(uuidList);
    }

    @Override
    public void getUserByRangeList(UserVo userVo, List<String> rangeList) {
        if (CollectionUtils.isNotEmpty(rangeList)) {
            List<String> roleList = new ArrayList<>();
            Set<String> teamSet = new HashSet<>();
            Set<String> parentTeamSet = new HashSet<>();
            Set<String> userSet = new HashSet<>();
            rangeList.forEach(r -> {
                if (r.startsWith(GroupSearch.ROLE.getValuePlugin())) {
                    roleList.add(GroupSearch.removePrefix(r));
                } else if (r.startsWith(GroupSearch.TEAM.getValuePlugin())) {
                    teamSet.add(GroupSearch.removePrefix(r));
                } else if (r.startsWith(GroupSearch.USER.getValuePlugin())) {
                    userSet.add(GroupSearch.removePrefix(r));
                }
            });
            if (CollectionUtils.isNotEmpty(roleList)) {
                List<RoleTeamVo> roleTeamVoList = roleMapper.getRoleTeamListByRoleUuidList(roleList);
                roleTeamVoList.forEach(rt -> {
                    if (rt.getCheckedChildren() == 1) {//如果组穿透
                        parentTeamSet.add(rt.getTeamUuid());
                    } else {
                        teamSet.add(rt.getTeamUuid());
                    }
                });
                List<RoleUserVo> roleUserVoList = roleMapper.getRoleUserListByRoleUuidList(roleList);
                roleUserVoList.forEach(ru -> {
                    userSet.add(ru.getUserUuid());
                });
            }
            userVo.setRangeList(rangeList.stream().map(Object::toString).collect(Collectors.toList()));
            userVo.setUserUuidList(new ArrayList<>(userSet));
            userVo.setTeamUuidList(new ArrayList<>(teamSet));
            userVo.setParentTeamUuidList(new ArrayList<>(parentTeamSet));
        }
    }

    @Override
    public List<UserVo> getUserListByRoleUuid(String roleUuid) {
        Set<String> userUuidSet = getUserUuidSetByRoleUuid(roleUuid);
        List<String> userUuidList;
        List<UserVo> allUserList = new ArrayList<>();
        if (userUuidSet.size() > 0) {
            List<String> allUserUuidList = new ArrayList<>(userUuidSet);
            for (int fromIndex = 0; fromIndex < allUserUuidList.size(); fromIndex += 100) {
                int toIndex = fromIndex + 100;
                if (toIndex > allUserUuidList.size()) {
                    toIndex = allUserUuidList.size();
                }
                userUuidList = allUserUuidList.subList(fromIndex, toIndex);
                List<UserVo> userList = userMapper.getUserListByUuidList(userUuidList);
                allUserList.addAll(userList);
            }
        }
        return allUserList;
    }

    @Override
    public Set<String> getUserUuidSetByRoleUuid(String roleUuid) {
        Set<String> userUuidSet = new HashSet<>();
        List<String> userUuidList = userMapper.getUserUuidListByRoleUuid(roleUuid);
        userUuidSet.addAll(userUuidList);
        Set<String> teamUuidSet = new HashSet<>();
        List<RoleTeamVo> roleTeamList = roleMapper.getRoleTeamListByRoleUuid(roleUuid);
        for (RoleTeamVo roleTeamVo : roleTeamList) {
            String teamUuid = roleTeamVo.getTeamUuid();
            if (Objects.equals(roleTeamVo.getCheckedChildren(), 1)) {
                TeamVo teamVo = teamMapper.getTeamByUuid(teamUuid);
                if (teamVo != null) {
                    teamUuidSet.add(teamUuid);
                    List<String> teamUuidList = teamMapper.getChildrenUuidListByLeftRightCode(teamVo.getLft(), teamVo.getRht());
                    teamUuidSet.addAll(teamUuidList);
                }
            } else {
                teamUuidSet.add(teamUuid);
            }
            if (teamUuidSet.size() >= 100) {
                userUuidList = userMapper.getUserUuidListByTeamUuidList(new ArrayList<>(teamUuidSet));
                userUuidSet.addAll(userUuidList);
                teamUuidSet.clear();
            }
        }
        if (teamUuidSet.size() > 0) {
            userUuidList = userMapper.getUserUuidListByTeamUuidList(new ArrayList<>(teamUuidSet));
            userUuidSet.addAll(userUuidList);
        }
        return userUuidSet;
    }

    @Override
    public Set<String> getTeamUuidSetByRoleUuid(String roleUuid) {
        Set<String> teamUuidSet = null;
        List<RoleTeamVo> roleTeamList = roleMapper.getRoleTeamListByRoleUuid(roleUuid);
        if (roleTeamList.size() > 0) {
            teamUuidSet = new HashSet<>();
            List<String> allTeamUuidList = roleTeamList.stream().map(RoleTeamVo::getTeamUuid).collect(Collectors.toList());
            List<String> list = roleTeamList.stream().filter(o -> Objects.equals(o.getCheckedChildren(), 0)).map(RoleTeamVo::getTeamUuid).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(list)) {
                teamUuidSet.addAll(list);  // 没有穿透的team直接add到teamUuidSet
                allTeamUuidList.removeAll(list);
                // 剩下有穿透的team，挨个找出其子节点并add到teamUuidSet
                if (allTeamUuidList.size() > 0) {
                    teamUuidSet.addAll(allTeamUuidList);
                    List<TeamVo> teamList = teamMapper.getTeamByUuidList(allTeamUuidList);
                    for (TeamVo team : teamList) {
                        teamUuidSet.addAll(teamMapper.getChildrenUuidListByLeftRightCode(team.getLft(), team.getRht()));
                    }
                }
            }
        }
        return teamUuidSet;
    }

    /**
     * 根据用户userId获取用户token
     *
     * @param user 用户userUuid | userId
     * @return token
     */
    @Override
    public String getUserTokenByUser(String user) {
        String token = SystemUserFactory.getUserTokenByUser(user);
        if (StringUtils.isBlank(token)) {
            token = userMapper.getUserTokenByUser(user);
        }
        return token;
    }


    /**
     * 根据用户
     *
     * @param user 用户userUuid
     * @return userVo
     */
    @Override
    public UserVo getUserByUser(String user) {
        UserVo userVo = SystemUserFactory.getUserVoByUser(user);
        if (userVo == null) {
            userVo = userMapper.getUserByUser(user);
        }
        return userVo;
    }


    /** 仅供内部授权聚合查询角色来源；过滤资料不足项，保留有效原始记录顺序及重复项。 */
    private List<UserAuthVo> getRoleAuthListByUserUuid(String userUuid) {
        if (StringUtils.isBlank(userUuid)) {
            return Collections.emptyList();
        }
        List<RoleAuthVo> authList = userMapper.searchUserRoleAuthByUserUuid(userUuid);
        List<String> roleUuidList = new ArrayList<>();
        for (RoleAuthVo auth : CollectionUtils.emptyIfNull(authList)) {
            roleUuidList.add(auth.getRoleUuid());
        }
        Map<String, String> roleNameMap = getRoleNameMap(roleUuidList);
        List<UserAuthVo> result = new ArrayList<>();
        for (RoleAuthVo auth : CollectionUtils.emptyIfNull(authList)) {
            String roleName = roleNameMap.get(auth.getRoleUuid());
            // 仅返回具有可信角色身份和名称的记录，保证单来源非空，不填充占位资料。
            if (StringUtils.isBlank(auth.getRoleUuid()) || StringUtils.isBlank(roleName)) {
                continue;
            }
            SourcePathVo source = new SourcePathVo(auth.getRoleUuid(), roleName, null, Collections.emptyList(), null);
            result.add(createUserAuthWithSourcePath(userUuid, auth.getAuthGroup(), auth.getAuth(), source));
        }
        return result;
    }

    /** 仅供内部授权聚合查询所属分组来源；过滤资料不足项，保留有效记录顺序及重复项。 */
    private List<UserAuthVo> getTeamAuthListByUserUuid(String userUuid) {
        if (StringUtils.isBlank(userUuid)) {
            return Collections.emptyList();
        }
        // 本次查询使用局部变量收集角色及所属分组关系，先处理祖先继承，再处理直接绑定。
        List<RoleVo> roleList = new ArrayList<>();
        Map<String, Set<String>> roleTeamUuidMap = new HashMap<>();
        List<String> teamUuidList = new ArrayList<>();
        for (TeamVo team : CollectionUtils.emptyIfNull(teamMapper.getTeamListByUserUuid(userUuid))) {
            teamUuidList.add(team.getUuid());
            for (RoleVo role : CollectionUtils.emptyIfNull(roleMapper.getParentTeamRoleListWithCheckedChildrenByTeam(team))) {
                roleList.add(role);
                roleTeamUuidMap.computeIfAbsent(role.getUuid(), key -> new LinkedHashSet<>()).add(team.getUuid());
            }
        }
        if (!teamUuidList.isEmpty()) {
            for (RoleVo role : CollectionUtils.emptyIfNull(roleMapper.getRoleListWithTeamByTeamUuidList(teamUuidList))) {
                roleList.add(role);
                for (TeamVo team : CollectionUtils.emptyIfNull(role.getTeamList())) {
                    roleTeamUuidMap.computeIfAbsent(role.getUuid(), key -> new LinkedHashSet<>()).add(team.getUuid());
                }
            }
        }
        if (roleList.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> roleUuidList = roleList.stream().map(RoleVo::getUuid).collect(Collectors.toList());
        List<RoleAuthVo> authList = roleMapper.searchRoleAuthByRoleUuidList(roleUuidList);
        if (CollectionUtils.isEmpty(authList)) {
            return Collections.emptyList();
        }
        // 资料按角色和所属分组各批量读取一次；路径重建及失败结果仅在本次调用内缓存。
        Map<String, String> roleNameMap = getRoleNameMap(roleUuidList);
        Map<String, SourcePathVo> teamSourceMap = getTeamSourceMap(teamUuidList);
        List<UserAuthVo> result = new ArrayList<>();
        for (RoleAuthVo auth : authList) {
            // 缺少角色身份或名称时整条原始授权不参与所属分组展开。
            String roleName = roleNameMap.get(auth.getRoleUuid());
            if (StringUtils.isBlank(auth.getRoleUuid()) || StringUtils.isBlank(roleName)) {
                continue;
            }
            for (String teamUuid : roleTeamUuidMap.getOrDefault(auth.getRoleUuid(), Collections.emptySet())) {
                SourcePathVo team = teamSourceMap.get(teamUuid);
                // 完整路径无法取得或重建时过滤本分组记录；lft允许为空，不影响来源可信性。
                if (StringUtils.isBlank(teamUuid) || team == null || CollectionUtils.isEmpty(team.getTeamPathNameList())
                        || team.getTeamPathNameList().stream().anyMatch(StringUtils::isBlank)) {
                    continue;
                }
                SourcePathVo source = new SourcePathVo(auth.getRoleUuid(), roleName, teamUuid, team.getTeamPathNameList(), team.getTeamLft());
                result.add(createUserAuthWithSourcePath(userUuid, auth.getAuthGroup(), auth.getAuth(), source));
            }
        }
        return result;
    }

    /** 批量读取可信角色名称，空集合不查询，原始名称不拼接展示文案。 */
    private Map<String, String> getRoleNameMap(List<String> roleUuidList) {
        Set<String> roleUuids = roleUuidList.stream().filter(StringUtils::isNotBlank)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<String, String> result = new HashMap<>();
        if (!roleUuids.isEmpty()) {
            for (RoleVo role : CollectionUtils.emptyIfNull(roleMapper.getRoleByUuidList(new ArrayList<>(roleUuids)))) {
                if (StringUtils.isNotBlank(role.getName())) {
                    result.put(role.getUuid(), role.getName());
                }
            }
        }
        return result;
    }

    /** 批量读取所属分组的来源资料，路径不足时重建；同分组包括失败结果均只解析一次。 */
    private Map<String, SourcePathVo> getTeamSourceMap(List<String> teamUuidList) {
        Map<String, SourcePathVo> result = new HashMap<>();
        Set<String> teamUuids = new LinkedHashSet<>(teamUuidList);
        if (!teamUuids.isEmpty()) {
            Set<String> visited = new HashSet<>();
            for (TeamVo team : CollectionUtils.emptyIfNull(teamMapper.getTeamByUuidList(new ArrayList<>(teamUuids)))) {
                if (!visited.add(team.getUuid())) {
                    continue;
                }
                List<String> names = StringUtils.isBlank(team.getUpwardNamePath())
                        ? getTeamSourcePath(team) : Arrays.asList(team.getUpwardNamePath().split("/", -1));
                if (CollectionUtils.isNotEmpty(names) && names.stream().noneMatch(StringUtils::isBlank)) {
                    result.put(team.getUuid(), new SourcePathVo(null, null, team.getUuid(), names, team.getLft()));
                }
            }
        }
        return result;
    }

    /** 新建中间权限记录，只复制单个来源，聚合来源列表保持null。 */
    private UserAuthVo createUserAuthWithSourcePath(String userUuid, String authGroup, String auth, SourcePathVo source) {
        UserAuthVo result = new UserAuthVo(userUuid, auth);
        result.setAuthGroup(authGroup);
        result.setSourcePath(source == null ? null : copySourcePath(source));
        return result;
    }

    /** 从中间记录的单来源生成聚合列表，保留本方法原有的结果范围及追加顺序。 */
    public List<UserAuthVo> getUserAuthListByUserUuid(String userUuid) {
        if (StringUtils.isBlank(userUuid)) {
            return Collections.emptyList();
        }
        UserVo userVo = userMapper.getUserByUuid(userUuid);
        if (userVo == null) {
            throw new UserNotFoundException(userUuid);
        }
        if (userVo.getIsSuperAdmin() != null && userVo.getIsSuperAdmin()) {
            List<UserAuthVo> userAuthList = new ArrayList<>();
            List<AuthBase> authBaseList = AuthFactory.getAuthList();
            for (AuthBase authBase : authBaseList) {
                String authGroupName = authBase.getAuthGroup();
                if (!TenantContext.get().getActiveModuleMap().containsKey(authGroupName)) {
                    continue;
                }
                List<SourcePathVo> sourcePathList = new ArrayList<>();
                // 每条权限使用独立的超级管理员来源，非空列表使授权页只读回显。
                SourcePathVo sourcePath = new SourcePathVo();
                sourcePath.setIsSuperAdmin(true);
                sourcePath.setTeamPathNameList(new ArrayList<>());
                sourcePathList.add(sourcePath);
                UserAuthVo userAuthVo = new UserAuthVo(userUuid, authBase.getAuthName(), authBase.getAuthGroup(), sourcePathList);
                userAuthList.add(userAuthVo);
            }
            return userAuthList;
        }
        List<UserAuthVo> userAuthList = userMapper.searchUserAuthByUserUuid(userUuid);
        List<UserAuthVo> directAuthList = getRoleAuthListByUserUuid(userUuid);
        List<UserAuthVo> teamAuthList = getTeamAuthListByUserUuid(userUuid);
        Map<String, UserAuthVo> userAuthMap = new HashMap<>();
        for (UserAuthVo auth : CollectionUtils.emptyIfNull(userAuthList)) {
            String key = auth.getAuthGroup() + "#" + auth.getAuth();
            UserAuthVo userAuthVo = userAuthMap.get(key);
            if (userAuthVo == null) {
                userAuthVo = new UserAuthVo(userUuid, auth.getAuth(), auth.getAuthGroup(), new ArrayList<>());
                userAuthMap.put(key, userAuthVo);
            }
        }
        // 角色和分组中间记录只读取sourcePath，输出对象只填充sourcePathList。
        for (UserAuthVo auth : CollectionUtils.emptyIfNull(directAuthList)) {
            List<SourcePathVo> sourcePathList = new ArrayList<>();
            if (auth.getSourcePath() != null) {
                sourcePathList.add(copySourcePath(auth.getSourcePath()));
            }
            String key = auth.getAuthGroup() + "#" + auth.getAuth();
            UserAuthVo userAuthVo = userAuthMap.get(key);
            if (userAuthVo == null) {
                userAuthVo = new UserAuthVo(userUuid, auth.getAuth(), auth.getAuthGroup(), sourcePathList);
                userAuthMap.put(key, userAuthVo);
            } else {
                userAuthVo.getSourcePathList().addAll(sourcePathList);
            }
        }
        for (UserAuthVo auth : CollectionUtils.emptyIfNull(teamAuthList)) {
            List<SourcePathVo> sourcePathList = new ArrayList<>();
            if (auth.getSourcePath() != null) {
                sourcePathList.add(copySourcePath(auth.getSourcePath()));
            }
            String key = auth.getAuthGroup() + "#" + auth.getAuth();
            UserAuthVo userAuthVo = userAuthMap.get(key);
            if (userAuthVo == null) {
                userAuthVo = new UserAuthVo(userUuid, auth.getAuth(), auth.getAuthGroup(), sourcePathList);
                userAuthMap.put(key, userAuthVo);
            } else {
                userAuthVo.getSourcePathList().addAll(sourcePathList);
            }
        }
        Map<AuthBase, List<UserAuthVo>> includeAuthListMap = new HashMap<>();
        // 包含链模板也由单来源承载，复制后再追加到最终来源列表。
        for (UserAuthVo userAuth : CollectionUtils.emptyIfNull(userAuthList)) {
            AuthBase root = AuthFactory.getAuthInstance(userAuth.getAuth());
            if (root != null) {
                List<UserAuthVo> includeAuthList = getIncludeAuthPaths(root);
                includeAuthListMap.put(root, includeAuthList);
                for (UserAuthVo auth : CollectionUtils.emptyIfNull(includeAuthList)) {
                    List<SourcePathVo> sourcePathList = new ArrayList<>();
                    if (auth.getSourcePath() != null) {
                        sourcePathList.add(copySourcePath(auth.getSourcePath()));
                    }
                    String key = auth.getAuthGroup() + "#" + auth.getAuth();
                    UserAuthVo userAuthVo = userAuthMap.get(key);
                    if (userAuthVo == null) {
                        userAuthVo = new UserAuthVo(userUuid, auth.getAuth(), auth.getAuthGroup(), sourcePathList);
                        userAuthMap.put(key, userAuthVo);
                    } else {
                        userAuthVo.getSourcePathList().addAll(sourcePathList);
                    }
                }
            }
        }
        for (UserAuthVo userAuth : CollectionUtils.emptyIfNull(directAuthList)) {
            AuthBase root = AuthFactory.getAuthInstance(userAuth.getAuth());
            if (root != null) {
                List<UserAuthVo> includeAuthList = includeAuthListMap.get(root);
                if (includeAuthList == null) {
                    includeAuthList = getIncludeAuthPaths(root);
                    includeAuthListMap.put(root, includeAuthList);
                }
                for (UserAuthVo auth : CollectionUtils.emptyIfNull(includeAuthList)) {
                    List<SourcePathVo> sourcePathList = new ArrayList<>();
                    if (userAuth.getSourcePath() != null) {
                        SourcePathVo sourcePathVo = copySourcePath(userAuth.getSourcePath());
                        if (auth.getSourcePath() != null) {
                            sourcePathVo.setIncludeAuthPathList(copySourcePath(auth.getSourcePath()).getIncludeAuthPathList());
                        }
                        sourcePathList.add(sourcePathVo);
                    }

                    String key = auth.getAuthGroup() + "#" + auth.getAuth();
                    UserAuthVo userAuthVo = userAuthMap.get(key);
                    if (userAuthVo == null) {
                        userAuthVo = new UserAuthVo(userUuid, auth.getAuth(), auth.getAuthGroup(), sourcePathList);
                        userAuthMap.put(key, userAuthVo);
                    } else {
                        userAuthVo.getSourcePathList().addAll(sourcePathList);
                    }
                }
            }
        }
        for (UserAuthVo userAuth : CollectionUtils.emptyIfNull(teamAuthList)) {
            AuthBase root = AuthFactory.getAuthInstance(userAuth.getAuth());
            if (root != null) {
                List<UserAuthVo> includeAuthList = includeAuthListMap.get(root);
                if (includeAuthList == null) {
                    includeAuthList = getIncludeAuthPaths(root);
                    includeAuthListMap.put(root, includeAuthList);
                }
                for (UserAuthVo auth : CollectionUtils.emptyIfNull(includeAuthList)) {
                    List<SourcePathVo> sourcePathList = new ArrayList<>();
                    if (userAuth.getSourcePath() != null) {
                        SourcePathVo sourcePathVo = copySourcePath(userAuth.getSourcePath());
                        if (auth.getSourcePath() != null) {
                            sourcePathVo.setIncludeAuthPathList(copySourcePath(auth.getSourcePath()).getIncludeAuthPathList());
                        }
                        sourcePathList.add(sourcePathVo);
                    }
                    String key = auth.getAuthGroup() + "#" + auth.getAuth();
                    UserAuthVo userAuthVo = userAuthMap.get(key);
                    if (userAuthVo == null) {
                        userAuthVo = new UserAuthVo(userUuid, auth.getAuth(), auth.getAuthGroup(), sourcePathList);
                        userAuthMap.put(key, userAuthVo);
                    } else {
                        userAuthVo.getSourcePathList().addAll(sourcePathList);
                    }
                }
            }
        }
        return new ArrayList<>(userAuthMap.values());
    }

    /** 每个根广度优先遍历，邻接编码排序使等长路径稳定；先访问去重阻止自包含和循环。 */
    private List<UserAuthVo> getIncludeAuthPaths(AuthBase root) {
        List<UserAuthVo> paths = new ArrayList<>();
        Set<String> visited = new LinkedHashSet<>();
        visited.add(root.getAuthName());
        Queue<List<AuthBase>> queue = new ArrayDeque<>();
        queue.add(Collections.singletonList(root));
        while (!queue.isEmpty()) {
            List<AuthBase> path = queue.remove();
            AuthBase current = path.get(path.size() - 1);
            List<AuthBase> children = new ArrayList<>();
            for (Class<? extends AuthBase> childClass : CollectionUtils.emptyIfNull(current.getIncludeAuths())) {
                AuthBase child = childClass == null ? null : AuthFactory.getAuthInstance(childClass.getSimpleName());
                if (child != null) {
                    children.add(child);
                }
            }
            children.sort(Comparator.comparing(AuthBase::getAuthName));
            for (AuthBase child : children) {
                if (!visited.add(child.getAuthName())) {
                    continue;
                }
                List<AuthBase> childPath = new ArrayList<>(path);
                childPath.add(child);
                List<JSONObject> authPath = new ArrayList<>();
                for (AuthBase node : childPath) {
                    JSONObject item = new JSONObject();
                    item.put("authGroup", node.getAuthGroup());
                    item.put("auth", node.getAuthName());
                    authPath.add(item);
                }
                // 此阶段只计算定义关系，不绑定用户或角色；sourcePath保存完整链，来源列表保持null。
                SourcePathVo source = new SourcePathVo(null, null, null, Collections.emptyList(), null);
                source.setIncludeAuthPathList(authPath);
                paths.add(createUserAuthWithSourcePath(null, child.getAuthGroup(), child.getAuthName(), source));
                queue.add(childPath);
            }
        }
        return paths;
    }

    /** 缺失路径时按祖先层级重建；资料不完整时留空，由前端回退原只读提示。 */
    private List<String> getTeamSourcePath(TeamVo team) {
        if (team.getLft() == null || team.getRht() == null) {
            return null;
        }
        List<TeamVo> ancestors = teamMapper.getAncestorsAndSelfByLftRht(team.getLft(), team.getRht(), null);
        if (CollectionUtils.isEmpty(ancestors)) {
            return null;
        }
        // 首节点也必须连接到根，避免缺失最上层祖先时把局部路径当作完整路径。
        String parentUuid = ancestors.get(0).getParentUuid();
        if (!TeamVo.ROOT_UUID.equals(parentUuid) && !TeamVo.ROOT_PARENTUUID.equals(parentUuid)) {
            return null;
        }
        List<String> names = new ArrayList<>();
        TeamVo previous = null;
        for (TeamVo ancestor : ancestors) {
            if (StringUtils.isBlank(ancestor.getName()) || (previous != null
                    && !previous.getUuid().equals(ancestor.getParentUuid()))) {
                return null;
            }
            names.add(ancestor.getName());
            previous = ancestor;
        }
        return team.getUuid().equals(previous.getUuid()) ? names : null;
    }

    /** 复制强类型来源及嵌套链节点，查询及最终聚合结果不与输入共享可变数据。 */
    private SourcePathVo copySourcePath(SourcePathVo source) {
        SourcePathVo result = new SourcePathVo(source.getRoleUuid(), source.getRoleName(), source.getTeamUuid(),
                source.getTeamPathNameList(), source.getTeamLft());
        // 单来源转为聚合来源时同步保留超级管理员身份。
        result.setIsSuperAdmin(source.getIsSuperAdmin());
        if (source.getIncludeAuthPathList() != null) {
            List<JSONObject> chain = new ArrayList<>();
            for (JSONObject node : source.getIncludeAuthPathList()) {
                chain.add(node == null ? null : new JSONObject(new HashMap<>(node)));
            }
            result.setIncludeAuthPathList(chain);
        }
        return result;
    }
}
