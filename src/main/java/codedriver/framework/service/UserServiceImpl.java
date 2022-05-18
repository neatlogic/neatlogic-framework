/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.service;

import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.dao.mapper.RoleMapper;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.RoleTeamVo;
import codedriver.framework.dto.RoleUserVo;
import codedriver.framework.dto.TeamVo;
import codedriver.framework.dto.UserVo;
import org.apache.commons.collections4.CollectionUtils;
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
            List<String> existUserUuidList = userMapper.checkUserUuidListIsExists(userUuidList, 1);
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
    public Set<String> getRoleTeamUuidSet(String roleUuid) {
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
}
