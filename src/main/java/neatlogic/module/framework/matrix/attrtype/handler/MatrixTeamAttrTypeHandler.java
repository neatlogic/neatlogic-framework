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
import neatlogic.framework.matrix.core.IMatrixAttrType;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class MatrixTeamAttrTypeHandler implements IMatrixAttrType {
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
}
