/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.matrix.attrtype.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.core.MatrixAttrTypeBase;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatrixTeamAttrTypeHandler extends MatrixAttrTypeBase {
    @Resource
    TeamMapper teamMapper;

    @Override
    public String getHandler() {
        return MatrixAttributeType.TEAM.getValue();
    }

    @Override
    public void getTextByValue(MatrixAttributeVo matrixAttribute, Object valueObj, JSONObject resultObj) {
        String value = valueObj.toString();
        TeamVo teamVo = teamMapper.getTeamByUuid(value);
        if (teamVo != null) {
            resultObj.put("text", teamVo.getName());
        }
    }

    @Override
    public String getValueWhenExport(Object value, MatrixAttributeVo attributeVo) {
        TeamVo team = teamMapper.getTeamByUuid(value.toString());
        if (team != null) {
            return team.getName();
        } else {
            return value.toString();
        }
    }

    @Override
    public Set<String> getRealValueBatch(MatrixAttributeVo matrixAttributeVo, Map<String, String> valueMap) {
        Set<String> repeatValueSet = new HashSet<>();
        List<String> needSearchValue = new ArrayList<>(valueMap.keySet());
        //通过uuid搜
        if (CollectionUtils.isNotEmpty(needSearchValue)) {
            List<TeamVo> teamVos = teamMapper.getTeamByUuidList(needSearchValue);
            if (CollectionUtils.isNotEmpty(teamVos)) {
                List<String> teamUuidList = teamVos.stream().map(TeamVo::getUuid).collect(Collectors.toList());
                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    if (teamUuidList.contains(entry.getKey())) {
                        valueMap.put(entry.getKey(), entry.getKey());
                        needSearchValue.remove(entry.getKey());
                    }
                }
            }
        }
        if(CollectionUtils.isEmpty(needSearchValue)){
            return repeatValueSet;
        }
        //通过name搜
        if (CollectionUtils.isNotEmpty(needSearchValue)) {
            List<TeamVo> teamVos = teamMapper.getTeamByNameList(needSearchValue);
            if (CollectionUtils.isNotEmpty(teamVos)) {
                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    List<TeamVo> teamTmpList = teamVos.stream().filter(t-> Objects.equals(t.getName(),entry.getKey())).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(teamTmpList)){
                        if(teamTmpList.size() == 1){
                            valueMap.put(entry.getKey(), teamTmpList.get(0).getUuid());
                        }else{
                            repeatValueSet.add(entry.getKey());
                        }
                        needSearchValue.remove(entry.getKey());
                    }
                }
            }
        }
        if(CollectionUtils.isEmpty(needSearchValue)){
            return repeatValueSet;
        }
        //通过upwardNamePath搜
        if (CollectionUtils.isNotEmpty(needSearchValue)) {
            List<TeamVo> teamVos = teamMapper.getTeamByUpwardNamePathList(needSearchValue);
            if (CollectionUtils.isNotEmpty(teamVos)) {
                Map<String, String> teamUpwardNamePathUuidMap = teamVos.stream().collect(Collectors.toMap(TeamVo::getUpwardNamePath, TeamVo::getUuid));
                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    if (teamUpwardNamePathUuidMap.containsKey(entry.getKey())) {
                        valueMap.put(entry.getKey(), teamUpwardNamePathUuidMap.get(entry.getKey()));
                        needSearchValue.remove(entry.getKey());
                    }
                }
            }
        }
        return repeatValueSet;
    }
}
