/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.RoleVo;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.util.AviatorEvaluatorUtil;
import neatlogic.framework.util.HeaderUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author linbq
 * @since 2021/8/2 20:22
 **/
@RootComponent
public class AuthenticationInfoServiceImpl implements AuthenticationInfoService {
    private TeamMapper teamMapper;
    private RoleMapper roleMapper;
    static Logger logger = LoggerFactory.getLogger(AuthenticationInfoServiceImpl.class);

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
     *
     * @param userUuid 用户uuid
     */
    @Override
    public AuthenticationInfoVo getAuthenticationInfo(String userUuid) {
        return getAuthenticationInfo(userUuid, true, null);
    }

    @Override
    public AuthenticationInfoVo getAuthenticationInfo(String userUuid, Boolean isRuleRole) {
        return getAuthenticationInfo(userUuid, isRuleRole, null);
    }

    @Override
    public AuthenticationInfoVo getAuthenticationInfo(String userUuid, Boolean isRuleRole, JSONObject originHeader) {
        List<String> teamUuidList = teamMapper.getTeamUuidListByUserUuid(userUuid);
        List<String> roleUuidList = roleMapper.getRoleUuidListByUserUuid(userUuid);
        Set<String> headerSet = new HashSet<>(); //使用到的header
        Set<String> roleUuidSet = new HashSet<>(roleUuidList);
        getTeamUuidListAndRoleUuidList(teamUuidList, roleUuidSet);
        if (Boolean.TRUE.equals(isRuleRole) && CollectionUtils.isNotEmpty(roleUuidSet)) {
            roleUuidList = removeInValidRoleUuidList(new ArrayList<>(roleUuidSet), headerSet, originHeader);
        } else {
            roleUuidList = new ArrayList<>(roleUuidSet);
        }
        return new AuthenticationInfoVo(userUuid, teamUuidList, roleUuidList, headerSet, originHeader);
    }

    /**
     * 补充teamUuidList roleUuidSet
     *
     * @param teamUuidList 组uuid列表
     * @param roleUuidSet  角色列表
     */
    private void getTeamUuidListAndRoleUuidList(List<String> teamUuidList, Set<String> roleUuidSet) {
        if (CollectionUtils.isNotEmpty(teamUuidList)) {
            List<String> teamRoleUuidList = roleMapper.getRoleUuidListByTeamUuidList(teamUuidList);
            roleUuidSet.addAll(teamRoleUuidList);
            Set<String> upwardUuidSet = getTeamSetWithParents(teamUuidList);
            if (CollectionUtils.isNotEmpty(upwardUuidSet)) {
                teamRoleUuidList = roleMapper.getRoleUuidListByTeamUuidListAndCheckedChildren(new ArrayList<>(upwardUuidSet), 1);
                roleUuidSet.addAll(teamRoleUuidList);
            }
        }
    }

    @Override
    public Set<String> getTeamSetWithParents(List<String> teamUuidList) {
        Set<String> upwardUuidSet = new HashSet<>();
        List<TeamVo> teamList = teamMapper.getTeamByUuidList(teamUuidList);
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
        return upwardUuidSet;
    }

    /**
     * 查询用户鉴权时，需要用到到userUuidList、teamUuidList、roleUuidList，其中roleUuidList包含用户所在分组的拥护角色列表。
     *
     * @param userUuidList 用户uuid列表
     */
    @Override
    public AuthenticationInfoVo getAuthenticationInfo(List<String> userUuidList) {
        Set<String> teamUuidSet = new HashSet<>();
        Set<String> roleUuidSet = new HashSet<>();
        Set<String> headerSet = new HashSet<>(); //使用到的header
        for (String userUuid : userUuidList) {
            List<String> userTeamUuidList = teamMapper.getTeamUuidListByUserUuid(userUuid);
            teamUuidSet.addAll(userTeamUuidList);
            List<String> userRoleUuidList = roleMapper.getRoleUuidListByUserUuid(userUuid);
            roleUuidSet.addAll(userRoleUuidList);
            getTeamUuidListAndRoleUuidList(userTeamUuidList, roleUuidSet);
        }
        List<String> roleUuidList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roleUuidSet)) {
            roleUuidList = removeInValidRoleUuidList(new ArrayList<>(roleUuidSet), headerSet, null);
        }
        return new AuthenticationInfoVo(userUuidList, new ArrayList<>(teamUuidSet), roleUuidList, headerSet);
    }


    /**
     * 去掉不满足规则的角色
     *
     * @param roleUuidList 角色
     * @param headerSet    合法的header
     * @param originHeader 原来合法的header
     */
    private List<String> removeInValidRoleUuidList(List<String> roleUuidList, Set<String> headerSet, JSONObject originHeader) {
        if (MapUtils.isEmpty(originHeader)) {
            originHeader = HeaderUtil.getHeaders();
        }
        //只保留符合指定前缀的header
        if (MapUtils.isNotEmpty(originHeader)) {
            headerSet.addAll(originHeader.keySet().stream().filter(o -> o.startsWith(Config.HEADER_RULE_PREFIX())).collect(Collectors.toSet()));
        }
        List<String> validRoleUuidList = new ArrayList<>();
        List<RoleVo> roleVos = roleMapper.getRoleByUuidList(roleUuidList);
        for (RoleVo ro : roleVos) {
            String rule = ro.getRule();
            if (StringUtils.isNotBlank(rule)) {
                try {
                    if (AviatorEvaluatorUtil.evaluateBoolean(rule, originHeader)) {
                        validRoleUuidList.add(ro.getUuid());
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                validRoleUuidList.add(ro.getUuid());
            }
        }
        return validRoleUuidList;
    }

}
