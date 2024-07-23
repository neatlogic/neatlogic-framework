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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.dao.cache.UserSessionCache;
import neatlogic.framework.dao.mapper.*;
import neatlogic.framework.dto.*;
import neatlogic.framework.util.Md5Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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

    @Resource
    private AuthenticationInfoService authenticationInfoService;

    @Resource
    private UserSessionContentMapper userSessionContentMapper;

    @Resource
    private UserSessionMapper userSessionMapper;

    @Resource
    private UserSessionService userSessionService;

    /**
     * @Description: 根据用户uuid集合与分组uuid集合查询激活的用户uuid
     * @Author: laiwt
     * @Date: 2021/3/5 17:06
     * @Params: [userUuidList, teamUuidList]
     * @Returns: java.util.Set<java.lang.String>
     **/
    @Override
    public Set<String> getUserUuidSetByUserUuidListAndTeamUuidList(List<String> userUuidList, List<String> teamUuidList) {
        Set<String> uuidList = new HashSet<>();
        if (CollectionUtils.isNotEmpty(userUuidList)) {
            List<String> existUserUuidList = userMapper.getUserUuidListByUuidListAndIsActive(userUuidList, 1);
            if (CollectionUtils.isNotEmpty(existUserUuidList)) {
                uuidList.addAll(new HashSet<>(existUserUuidList));
            }
        }
        if (CollectionUtils.isNotEmpty(teamUuidList)) {
            List<String> list = userMapper.getUserUuidListByTeamUuidList(teamUuidList);
            if (CollectionUtils.isNotEmpty(list)) {
                uuidList.addAll(new HashSet<>(list));
            }
        }
        return uuidList;
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
        String token = SystemUser.getUserTokenByUser(user);
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
        UserVo userVo = SystemUser.getUserVoByUser(user);
        if (userVo == null) {
            userVo = userMapper.getUserByUser(user);
        }
        return userVo;
    }

    @Override
    public void updateUserCacheAndSessionByUserUuid(String userUuid) {
        List<UserSessionVo> userSessionVoList = userSessionMapper.getUserSessionByUuid(userUuid);
        Map<String, AuthenticationInfoVo> sessionTokenHashAuthInfoHashMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userSessionVoList)) {
            for (UserSessionVo userSessionVo : userSessionVoList) {
                AuthenticationInfoVo authenticationInfoVo = null;
                String token = userSessionContentMapper.getUserSessionContentByHash(userSessionVo.getTokenHash());
                JSONObject tokenJson;
                try {
                    tokenJson = JSON.parseObject(token);
                    if (MapUtils.isNotEmpty(tokenJson)) {
                        JSONObject originHeader = tokenJson.getJSONObject("originHeader");
                        authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userUuid, true, originHeader);
                    }
                } catch (Exception ignored) {
                }
                sessionTokenHashAuthInfoHashMap.put(userSessionVo.getTokenHash(), authenticationInfoVo);
            }
            if (MapUtils.isNotEmpty(sessionTokenHashAuthInfoHashMap)) {
                for (Map.Entry<String, AuthenticationInfoVo> entry : sessionTokenHashAuthInfoHashMap.entrySet()) {
                    String tokenHash = entry.getKey();
                    AuthenticationInfoVo authenticationInfoVo = entry.getValue();
                    if (authenticationInfoVo != null && authenticationInfoVo.isNotNull()) {
                        String authenticationInfoStr = JSON.toJSONString(authenticationInfoVo);
                        String authInfoHash = Md5Util.encryptMD5(authenticationInfoStr);
                        userSessionContentMapper.insertUserSessionContent(new UserSessionContentVo(authInfoHash, authenticationInfoStr));
                        userSessionService.updateUserSessionAuthInfoHashByTokenHash(tokenHash, authInfoHash);
                        UserSessionCache.addItem(tokenHash, authenticationInfoStr);
                    } else {
                        userSessionService.updateUserSessionAuthInfoHashByTokenHash(tokenHash, null);
                        UserSessionCache.addItem(tokenHash, JSON.toJSONString(new AuthenticationInfoVo(userUuid)));
                    }
                }
            }
        }
    }

}
