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
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dto.RoleVo;
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.core.MatrixAttrTypeBase;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatrixRoleAttrTypeHandler extends MatrixAttrTypeBase {
    @Resource
    RoleMapper roleMapper;

    @Override
    public String getHandler() {
        return MatrixAttributeType.ROLE.getValue();
    }

    @Override
    public void getTextByValue(MatrixAttributeVo matrixAttribute, Object valueObj, JSONObject resultObj) {
        String value = valueObj.toString();
        RoleVo roleVo = roleMapper.getRoleByUuid(value);
        if (roleVo != null) {
            resultObj.put("text", roleVo.getName());
        }
    }

    @Override
    public String getValueWhenExport(Object value, MatrixAttributeVo attributeVo) {
        RoleVo role = roleMapper.getRoleByUuid(value.toString());
        if (role != null) {
            return role.getName();
        } else {
            return value.toString();
        }
    }

    @Override
    public Set<String> getRealValueBatch(MatrixAttributeVo matrixAttributeVo, Map<String, String> valueMap) {
        List<String> needSearchValue = new ArrayList<>(valueMap.keySet());
        //通过uuid搜
        if (CollectionUtils.isNotEmpty(needSearchValue)) {
            List<RoleVo> roleVos = roleMapper.getRoleByUuidList(needSearchValue);
            if (CollectionUtils.isNotEmpty(roleVos)) {
                List<String> roleUuidList = roleVos.stream().map(RoleVo::getUuid).collect(Collectors.toList());
                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    if (roleUuidList.contains(entry.getKey())) {
                        valueMap.put(entry.getKey(), entry.getKey());
                        needSearchValue.remove(entry.getKey());
                    }
                }
            }
        }
        if(CollectionUtils.isEmpty(needSearchValue)){
            return Collections.emptySet();
        }
        //通过name搜
        if (CollectionUtils.isNotEmpty(needSearchValue)) {
            List<RoleVo> roleVos = roleMapper.getRoleByNameList(needSearchValue);
            if (CollectionUtils.isNotEmpty(roleVos)) {
                Map<String, String> roleNameUuidMap = roleVos.stream().collect(Collectors.toMap(RoleVo::getName, RoleVo::getUuid));
                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    if (roleNameUuidMap.containsKey(entry.getKey())) {
                        valueMap.put(entry.getKey(), roleNameUuidMap.get(entry.getKey()));
                        needSearchValue.remove(entry.getKey());
                    }
                }
            }
        }
        return Collections.emptySet();
    }
}
