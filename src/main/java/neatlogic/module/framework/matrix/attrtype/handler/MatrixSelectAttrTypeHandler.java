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
