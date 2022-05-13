/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.service;

import codedriver.framework.common.RootComponent;
import codedriver.framework.dao.mapper.RoleMapper;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dto.AuthenticationInfoVo;
import codedriver.framework.dto.TeamVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author linbq
 * @since 2021/8/2 20:22
 **/
@RootComponent
public class AuthenticationInfoServiceImpl implements AuthenticationInfoService {
    private TeamMapper teamMapper;
    private RoleMapper roleMapper;

    @Resource
    public void setTeamMapper(TeamMapper _teamMapper) {
        teamMapper = _teamMapper;
    }

    @Resource
    public void setRoleMapper(RoleMapper _roleMapper) {
        roleMapper = _roleMapper;
    }

    /**
     * 查询用户鉴权时，需要用到到userUuid、teamUuidList、roleUuidList，其中roleUuidList包含用户所在分组的拥护角色列表。
     * @param userUuid
     * @return
     */
    @Override
    public AuthenticationInfoVo getAuthenticationInfo(String userUuid){
        AuthenticationInfoVo authenticationInfoVo = new AuthenticationInfoVo();
        authenticationInfoVo.setUserUuid(userUuid);
        List<String> teamUuidList = teamMapper.getTeamUuidListByUserUuid(userUuid);
        authenticationInfoVo.setTeamUuidList(teamUuidList);
        List<String> userRoleUuidList = roleMapper.getRoleUuidListByUserUuid(userUuid);
        Set<String> roleUuidSet = new HashSet<>(userRoleUuidList);
        if (CollectionUtils.isNotEmpty(teamUuidList)) {
            List<String> teamRoleUuidList = roleMapper.getRoleUuidListByTeamUuidListAndCheckedChildren(teamUuidList, null);
            roleUuidSet.addAll(teamRoleUuidList);
            Set<String> upwardUuidSet = new HashSet<>();
            List<TeamVo> teamList =  teamMapper.getTeamByUuidList(teamUuidList);
            for (TeamVo teamVo : teamList) {
                String upwardUuidPath = teamVo.getUpwardUuidPath();
                if (StringUtils.isNotBlank(upwardUuidPath)) {
                    String[] upwardUuidArray = upwardUuidPath.split(",");
                    for (String upwardUuid : upwardUuidArray) {
                        if (!upwardUuid.equals(teamVo.getUuid())) {
                            upwardUuidSet.add(upwardUuid);
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(upwardUuidSet)) {
                teamRoleUuidList = roleMapper.getRoleUuidListByTeamUuidListAndCheckedChildren(new ArrayList<>(upwardUuidSet), 1);
                roleUuidSet.addAll(teamRoleUuidList);
            }
        }
        List<String> roleUuidList = new ArrayList<>(roleUuidSet);
        authenticationInfoVo.setRoleUuidList(roleUuidList);
        return authenticationInfoVo;
    }

    /**
     * 查询用户鉴权时，需要用到到userUuidList、teamUuidList、roleUuidList，其中roleUuidList包含用户所在分组的拥护角色列表。
     * @param userUuidList
     * @return
     */
    @Override
    public AuthenticationInfoVo getAuthenticationInfo(List<String> userUuidList){
        AuthenticationInfoVo authenticationInfoVo = new AuthenticationInfoVo();
        authenticationInfoVo.setUserUuidList(userUuidList);
        Set<String> teamUuidSet = new HashSet<>();
        Set<String> roleUuidSet = new HashSet<>();
        for (String userUuid : userUuidList) {
            List<String> teamUuidList = teamMapper.getTeamUuidListByUserUuid(userUuid);
            teamUuidSet.addAll(teamUuidList);
            List<String> userRoleUuidList = roleMapper.getRoleUuidListByUserUuid(userUuid);
            roleUuidSet.addAll(userRoleUuidList);
            if (CollectionUtils.isNotEmpty(teamUuidList)) {
                List<String> teamRoleUuidList = roleMapper.getRoleUuidListByTeamUuidListAndCheckedChildren(teamUuidList, null);
                roleUuidSet.addAll(teamRoleUuidList);
                Set<String> upwardUuidSet = new HashSet<>();
                List<TeamVo> teamList =  teamMapper.getTeamByUuidList(teamUuidList);
                for (TeamVo teamVo : teamList) {
                    String upwardUuidPath = teamVo.getUpwardUuidPath();
                    if (StringUtils.isNotBlank(upwardUuidPath)) {
                        String[] upwardUuidArray = upwardUuidPath.split(",");
                        for (String upwardUuid : upwardUuidArray) {
                            if (!upwardUuid.equals(teamVo.getUuid())) {
                                upwardUuidSet.add(upwardUuid);
                            }
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(upwardUuidSet)) {
                    teamRoleUuidList = roleMapper.getRoleUuidListByTeamUuidListAndCheckedChildren(new ArrayList<>(upwardUuidSet), 1);
                    roleUuidSet.addAll(teamRoleUuidList);
                }
            }
        }
        authenticationInfoVo.setTeamUuidList(new ArrayList<>(teamUuidSet));
        authenticationInfoVo.setRoleUuidList(new ArrayList<>(roleUuidSet));
        return authenticationInfoVo;
    }
}
