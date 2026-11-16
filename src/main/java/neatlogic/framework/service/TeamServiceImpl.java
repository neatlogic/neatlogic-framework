/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.service;

import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.TeamUserTitleVo;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.dto.UserTitleVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements TeamService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private TeamMapper teamMapper;

    @Override
    public List<String> getParentPathList(TeamVo team) {
        List<String> pathNameList = new ArrayList<>();
        List<TeamVo> ancestorsAndSelf = teamMapper.getAncestorsAndSelfByLftRht(team.getLft(), team.getRht(), null);
        if (CollectionUtils.isNotEmpty(ancestorsAndSelf)) {
            for (TeamVo ancestor : ancestorsAndSelf) {
                pathNameList.add(ancestor.getName());
            }
//            if (CollectionUtils.isNotEmpty(pathNameList)) {
//                team.setFullPath(String.join("->", pathNameList));
//                team.setParentPathList(pathNameList);
//            }
        }
        return pathNameList;
    }

    @Override
    public  void deleteTeamUserTitleByTeamUuid(String teamUuid){
        List<TeamUserTitleVo> teamUserTitleVoList = teamMapper.getTeamUserTitleListByTeamUuid(teamUuid);
        teamMapper.deleteTeamUserTitleByTeamUuid(teamUuid);
        if(CollectionUtils.isNotEmpty(teamUserTitleVoList)) {
            List<UserTitleVo> userTitleVoList = userMapper.getUserTitleListLockByTitleNameList(teamUserTitleVoList.stream().map(TeamUserTitleVo::getTitle).collect(Collectors.toList()));
            for (UserTitleVo userTitleVo : userTitleVoList) {
                if (teamMapper.checkTitleIsReferenceByTitleId(userTitleVo.getId()) == 0) {
                    userMapper.deleteUserTitleByName(userTitleVo.getName());
                }
            }
        }
    }
}
