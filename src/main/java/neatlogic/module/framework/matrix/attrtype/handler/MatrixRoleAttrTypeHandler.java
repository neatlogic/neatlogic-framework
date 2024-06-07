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
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dto.RoleVo;
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.core.MatrixAttrTypeBase;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public String getValueWhenExport(String value) {
        RoleVo role = roleMapper.getRoleByUuid(value);
        if (role != null) {
            return role.getName();
        } else {
            return value;
        }
    }

    @Override
    public void getRealValueBatch(MatrixAttributeVo matrixAttributeVo, Map<String, String> valueMap) {
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
            return;
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
    }
}
