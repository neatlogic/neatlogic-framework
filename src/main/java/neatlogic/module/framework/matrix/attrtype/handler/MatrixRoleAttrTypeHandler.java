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
import neatlogic.framework.matrix.core.IMatrixAttrType;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class MatrixRoleAttrTypeHandler implements IMatrixAttrType {
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
}
