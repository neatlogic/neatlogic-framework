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

package neatlogic.module.framework.groupsearch.handler;

import neatlogic.framework.common.constvalue.DeviceType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.util.CommonUtil;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dto.RoleTeamVo;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.restful.groupsearch.core.GroupSearchOptionVo;
import neatlogic.framework.restful.groupsearch.core.GroupSearchVo;
import neatlogic.framework.restful.groupsearch.core.IGroupSearchHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamGroupHandler implements IGroupSearchHandler {
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private RoleMapper roleMapper;

    @Override
    public String getName() {
        return GroupSearch.TEAM.getValue();
    }

    @Override
    public String getLabel() {
        return GroupSearch.TEAM.getText();
    }

    @Override
    public String getHeader() {
        return getName() + "#";
    }

    @Override
    public List<GroupSearchOptionVo> search(GroupSearchVo groupSearchVo) {
        //总显示选项个数
        Integer total = groupSearchVo.getTotal();
        if (total == null) {
            total = 18;
        }
        List<TeamVo> teamList = new ArrayList<TeamVo>();
        TeamVo teamVo = new TeamVo();
        teamVo.setNeedPage(true);
        teamVo.setPageSize(total);
        teamVo.setCurrentPage(1);
        teamVo.setKeyword(groupSearchVo.getKeyword());
        teamVo.setIsDelete(0);
        //如果存在rangeList 则需要过滤option
        List<String> rangeList = groupSearchVo.getRangeList();
        if (CollectionUtils.isNotEmpty(rangeList)) {
            List<String> roleList = new ArrayList<>();
            Set<String> teamSet = new HashSet<>();
            Set<String> parentTeamSet = new HashSet<>();
            rangeList.forEach(r -> {
                if (r.startsWith(GroupSearch.ROLE.getValuePlugin())) {
                    roleList.add(GroupSearch.removePrefix(r));
                } else if (r.startsWith(GroupSearch.TEAM.getValuePlugin())) {
                    teamSet.add(GroupSearch.removePrefix(r));
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
            }
            if (CollectionUtils.isEmpty(teamSet) && CollectionUtils.isEmpty(parentTeamSet)) {//如果rangList不为空 但teamUuidList和parenTeamUuidList都为空则 无需返回分组
                teamSet.add("team#no_team");
            }
            teamVo.setTeamUuidList(new ArrayList<>(teamSet));
            teamVo.setParentTeamUuidList(new ArrayList<>(parentTeamSet));
            teamVo.setRangeList(rangeList.stream().map(Object::toString).collect(Collectors.toList()));
        }
        teamList = teamMapper.searchTeam(teamVo);
        setFullPathAndParentName(teamList);
        return convertGroupSearchOption(teamList);
    }

    @Override
    public List<GroupSearchOptionVo> reload(GroupSearchVo groupSearchVo) {
        List<TeamVo> teamList = new ArrayList<TeamVo>();
        List<String> teamUuidList = new ArrayList<String>();
        for (String value : groupSearchVo.getValueList()) {
            if (value.startsWith(getHeader())) {
                teamUuidList.add(value.replace(getHeader(), StringUtils.EMPTY));
            }
        }
        if (!teamUuidList.isEmpty()) {
            teamList = teamMapper.getTeamByUuidList(teamUuidList);
            setFullPathAndParentName(teamList);
        }
        return convertGroupSearchOption(teamList);
    }

    private List<GroupSearchOptionVo> convertGroupSearchOption(List<TeamVo> teamList) {
        List<GroupSearchOptionVo> dataList = new ArrayList<>();
        for (TeamVo team : teamList) {
            GroupSearchOptionVo groupSearchOptionVo = new GroupSearchOptionVo();
            groupSearchOptionVo.setValue(getHeader() + team.getUuid());
            if (DeviceType.MOBILE.getValue().equals(CommonUtil.getDevice())) {
                groupSearchOptionVo.setText(StringUtils.isNotBlank(team.getParentName())
                        ? team.getName() + "(" + team.getParentName() + ")"
                        : team.getName());
            } else {
                groupSearchOptionVo.setText(team.getName());
            }
            groupSearchOptionVo.setFullPath(team.getFullPath());
            groupSearchOptionVo.setParentPathList(team.getParentPathList());
            dataList.add(groupSearchOptionVo);
        }
        return dataList;
    }

    @Override
    public int getSort() {
        return 3;
    }

    @Override
    public Boolean isLimit() {
        return true;
    }

    /**
     * @Description: 查询分组的全路径与父分组名称
     * @Author: laiwt
     * @Date: 2021/2/1 15:51
     * @Params: [teamList]
     * @Returns: void
     **/
    private void setFullPathAndParentName(List<TeamVo> teamList) {
        if (CollectionUtils.isNotEmpty(teamList)) {
            List<TeamVo> nameRepeatedCount = teamMapper.getRepeatTeamNameByNameList(teamList.stream().map(TeamVo::getName).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(nameRepeatedCount)) {
                Map<String, Integer> map = nameRepeatedCount.stream().collect(Collectors.toMap(TeamVo::getName, TeamVo::getNameRepeatCount));
                for (TeamVo team : teamList) {
                    List<TeamVo> ancestorsAndSelf = teamMapper.getAncestorsAndSelfByLftRht(team.getLft(), team.getRht(), null);
                    if (CollectionUtils.isNotEmpty(ancestorsAndSelf)) {
                        List<String> pathNameList = new ArrayList<>();
                        for (TeamVo ancestor : ancestorsAndSelf) {
                            pathNameList.add(ancestor.getName());
                        }
                        if (CollectionUtils.isNotEmpty(pathNameList)) {
                            team.setFullPath(String.join("->", pathNameList));
                            team.setParentPathList(pathNameList);
                        }
                    }
                    /* 如果有重名的分组，找出其父分组的名称 */
                    if (MapUtils.isNotEmpty(map) && map.get(team.getName()) != null && map.get(team.getName()) > 1) {
                        TeamVo parent = teamMapper.getTeamByUuid(team.getParentUuid());
                        if (parent != null) {
                            team.setParentName(parent.getName());
                        }
                    }
                }
            }
        }
    }

}
