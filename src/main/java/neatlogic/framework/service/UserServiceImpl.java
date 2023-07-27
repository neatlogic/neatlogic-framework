/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */

package neatlogic.framework.service;

import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.RoleTeamVo;
import neatlogic.framework.dto.RoleUserVo;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.dto.UserVo;
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
     * @param userUuid 用户userUuid
     * @return token
     */
    @Override
    public String getUserTokenByUserUuid(String userUuid) {
        String token = SystemUser.getUserTokenByUserUuid(userUuid);
        if (StringUtils.isBlank(token)) {
            token = userMapper.getUserTokenByUserUuid(userUuid);
        }
        return token;
    }


    /**
     * 根据用户
     *
     * @param userUuid 用户userUuid
     * @return userVo
     */
    @Override
    public UserVo getUserByUserUuid(String userUuid) {
        UserVo user = SystemUser.getUserVoByUserUuid(userUuid);
        if (user == null) {
            user = userMapper.getUserByUserUuid(userUuid);
        }
        return user;
    }

}
