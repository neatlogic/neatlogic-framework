/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.service;

import codedriver.framework.common.RootComponent;
import codedriver.framework.dao.mapper.RoleMapper;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dto.AuthenticationInfoVo;

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

    @Override
    public AuthenticationInfoVo getAuthenticationInfo(String userUuid){
        AuthenticationInfoVo authenticationInfoVo = new AuthenticationInfoVo();
        authenticationInfoVo.setUserUuid(userUuid);
        List<String> teamUuidList = teamMapper.getTeamUuidListByUserUuid(userUuid);
        authenticationInfoVo.setTeamUuidList(teamUuidList);
        List<String> userRoleUuidList = roleMapper.getRoleUuidListByUserUuid(userUuid);
        List<String> teamRoleUuidList = roleMapper.getRoleUuidListByTeamUuidList(teamUuidList);
        Set<String> roleUuidSet = new HashSet<>();
        roleUuidSet.addAll(userRoleUuidList);
        roleUuidSet.addAll(teamRoleUuidList);
        List<String> roleUuidList = new ArrayList<>(roleUuidSet);
        authenticationInfoVo.setRoleUuidList(roleUuidList);
        return authenticationInfoVo;
    }

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
            List<String> teamRoleUuidList = roleMapper.getRoleUuidListByTeamUuidList(teamUuidList);
            roleUuidSet.addAll(userRoleUuidList);
            roleUuidSet.addAll(teamRoleUuidList);
        }
        authenticationInfoVo.setTeamUuidList(new ArrayList<>(teamUuidSet));
        authenticationInfoVo.setRoleUuidList(new ArrayList<>(roleUuidSet));
        return authenticationInfoVo;
    }
}
