/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    public String getValueWhenExport(String value) {
        TeamVo team = teamMapper.getTeamByUuid(value);
        if (team != null) {
            return team.getName();
        } else {
            return value;
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
