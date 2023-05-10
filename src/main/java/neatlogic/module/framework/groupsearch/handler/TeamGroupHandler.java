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

package neatlogic.module.framework.groupsearch.handler;

import neatlogic.framework.common.constvalue.DeviceType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.util.CommonUtil;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dto.RoleTeamVo;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.restful.groupsearch.core.IGroupSearchHandler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamGroupHandler implements IGroupSearchHandler {
    @Autowired
    private TeamMapper teamMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public String getName() {
        return GroupSearch.TEAM.getValue();
    }

    @Override
    public String getHeader() {
        return getName() + "#";
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> search(JSONObject jsonObj) {
        //总显示选项个数
        Integer total = jsonObj.getInteger("total");
        if (total == null) {
            total = 18;
        }
        List<TeamVo> teamList = new ArrayList<TeamVo>();
        TeamVo teamVo = new TeamVo();
        teamVo.setNeedPage(true);
        teamVo.setPageSize(total);
        teamVo.setCurrentPage(1);
        teamVo.setKeyword(jsonObj.getString("keyword"));
        teamVo.setIsDelete(0);
        //如果存在rangeList 则需要过滤option
        List<Object> rangeList = jsonObj.getJSONArray("rangeList");
        if (CollectionUtils.isNotEmpty(rangeList)) {
            List<String> roleList = new ArrayList<>();
            Set<String> teamSet = new HashSet<>();
            Set<String> parentTeamSet = new HashSet<>();
            rangeList.forEach(r -> {
                if (r.toString().startsWith(GroupSearch.ROLE.getValuePlugin())) {
                    roleList.add(GroupSearch.removePrefix(r.toString()));
                } else if (r.toString().startsWith(GroupSearch.TEAM.getValuePlugin())) {
                    teamSet.add(GroupSearch.removePrefix(r.toString()));
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
        return (List<T>) teamList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> reload(JSONObject jsonObj) {
        List<TeamVo> teamList = new ArrayList<TeamVo>();
        List<String> teamUuidList = new ArrayList<String>();
        for (Object value : jsonObj.getJSONArray("valueList")) {
            if (value.toString().startsWith(getHeader())) {
                teamUuidList.add(value.toString().replace(getHeader(), ""));
            }
        }
        if (teamUuidList.size() > 0) {
            teamList = teamMapper.getTeamByUuidList(teamUuidList);
            setFullPathAndParentName(teamList);
        }
        return (List<T>) teamList;
    }

    @Override
    public <T> JSONObject repack(List<T> teamList) {
        JSONObject teamObj = new JSONObject();
        teamObj.put("value", "team");
        teamObj.put("text", "分组");
        JSONArray teamArray = new JSONArray();
        for (T team : teamList) {
            JSONObject teamTmp = new JSONObject();
            teamTmp.put("value", getHeader() + ((TeamVo) team).getUuid());
            if (DeviceType.MOBILE.getValue().equals(CommonUtil.getDevice())) {
                teamTmp.put("text", StringUtils.isNotBlank(((TeamVo) team).getParentName())
                        ? ((TeamVo) team).getName() + "(" + ((TeamVo) team).getParentName() + ")"
                        : ((TeamVo) team).getName());
            } else {
                teamTmp.put("text", ((TeamVo) team).getName());
            }
            teamTmp.put("fullPath", ((TeamVo) team).getFullPath());
            teamTmp.put("parentPathList", ((TeamVo) team).getParentPathList());
            teamArray.add(teamTmp);
        }
        teamObj.put("sort", getSort());
        teamObj.put("dataList", teamArray);
        return teamObj;
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
            if(CollectionUtils.isNotEmpty(nameRepeatedCount)) {
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

    public static void main(String[] args) {
        TeamVo a = new TeamVo();
        a.setName("test");
        List<TeamVo> teamList = Arrays.asList(a);
        Map<String, Integer> map = teamList.stream().collect(Collectors.toMap(TeamVo::getName, TeamVo::getNameRepeatCount));
    }
}
