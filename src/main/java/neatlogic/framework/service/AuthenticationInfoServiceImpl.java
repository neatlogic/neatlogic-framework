/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.service;

import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.TeamVo;
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
        List<String> teamUuidList = teamMapper.getTeamUuidListByUserUuid(userUuid);
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
        return new AuthenticationInfoVo(userUuid, teamUuidList, new ArrayList<>(roleUuidSet));
    }

    /**
     * 查询用户鉴权时，需要用到到userUuidList、teamUuidList、roleUuidList，其中roleUuidList包含用户所在分组的拥护角色列表。
     * @param userUuidList
     * @return
     */
    @Override
    public AuthenticationInfoVo getAuthenticationInfo(List<String> userUuidList){
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
        return new AuthenticationInfoVo(userUuidList, new ArrayList<>(teamUuidSet), new ArrayList<>(roleUuidSet));
    }
}
