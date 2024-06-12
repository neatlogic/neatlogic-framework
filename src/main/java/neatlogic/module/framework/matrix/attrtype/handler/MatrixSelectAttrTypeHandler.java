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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.core.MatrixAttrTypeBase;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatrixSelectAttrTypeHandler extends MatrixAttrTypeBase {
    @Override
    public String getHandler() {
        return MatrixAttributeType.SELECT.getValue();
    }

    @Override
    public void getTextByValue(MatrixAttributeVo matrixAttribute, Object valueObj, JSONObject resultObj) {
        String value = valueObj.toString();
        if (matrixAttribute != null) {
            JSONObject config = matrixAttribute.getConfig();
            if (MapUtils.isNotEmpty(config)) {
                JSONArray dataList = config.getJSONArray("dataList");
                if (CollectionUtils.isNotEmpty(dataList)) {
                    for (int i = 0; i < dataList.size(); i++) {
                        JSONObject data = dataList.getJSONObject(i);
                        if (Objects.equals(value, data.getString("value"))) {
                            resultObj.put("text", data.getString("text"));
                        }
                    }
                }
            }
        }
    }

    @Override
    public Set<String> getRealValueBatch(MatrixAttributeVo matrixAttributeVo, Map<String, String> valueMap) {
        JSONObject config = matrixAttributeVo.getConfig();
        Map<String, String> configTextValueMap = new HashMap<>();
        if (MapUtils.isNotEmpty(config)) {
            JSONArray dataList = config.getJSONArray("dataList");
            for (int i = 0; i < dataList.size(); i++) {
                JSONObject dataObj = dataList.getJSONObject(i);
                configTextValueMap.put(dataObj.getString("text"), dataObj.getString("value"));
            }
            for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                String value = entry.getKey();
                if (configTextValueMap.containsValue(value)) {
                    valueMap.put(value, value);
                } else if (configTextValueMap.containsKey(value)) {
                    valueMap.put(value, configTextValueMap.get(value));
                }
            }
        }
        return Collections.emptySet();
    }
}
