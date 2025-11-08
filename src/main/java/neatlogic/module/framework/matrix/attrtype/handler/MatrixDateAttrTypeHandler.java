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
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.core.MatrixAttrTypeBase;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MatrixDateAttrTypeHandler extends MatrixAttrTypeBase {
    @Override
    public String getHandler() {
        return MatrixAttributeType.DATE.getValue();
    }

    @Override
    public String getValueWhenExport(Object value, MatrixAttributeVo attributeVo) {
        String pattern = TimeUtil.YYYY_MM_DD_HH_MM_SS;
        JSONObject config = attributeVo.getConfig();
        if (MapUtils.isNotEmpty(config)) {
            String format = config.getString("format");
            if (StringUtils.isNotBlank(format)) {
                String styleType = config.getString("styleType");
                if (StringUtils.isNotBlank(styleType) && !Objects.equals(styleType, "-")) {
                    if ("|".equals(styleType)) {
                        styleType = "";
                    }
                    format = format.replace("-", styleType);
                }
                pattern = format;
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        if (value instanceof Date date) {
            value = simpleDateFormat.format(date);
        }
        return value.toString();
    }

    @Override
    public void getTextByValue(MatrixAttributeVo matrixAttribute, Object valueObj, JSONObject resultObj) {
        if (valueObj != null) {
            if (valueObj instanceof Date) {
                String format = TimeUtil.YYYY_MM_DD_HH_MM_SS;
                JSONObject config = matrixAttribute.getConfig();
                if (MapUtils.isNotEmpty(config)) {
                    format = config.getString("format");
                    if (StringUtils.isBlank(format)) {
                        format = TimeUtil.YYYY_MM_DD_HH_MM_SS;
                    }
                    String styleType = config.getString("styleType");
                    if (StringUtils.isNotBlank(styleType) && !Objects.equals(styleType, "-")) {
                        if ("|".equals(styleType)) {
                            styleType = "";
                        }
                        format = format.replace("-", styleType);
                    }
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                String newValue = simpleDateFormat.format(valueObj);
                resultObj.put("text", newValue);
            } else {
                resultObj.put("text", valueObj);
            }
        }
    }

    @Override
    public Set<String> getRealValueBatch(MatrixAttributeVo matrixAttributeVo, Map<String, String> valueMap) {
//        valueMap.replaceAll((k, v) -> k);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtil.YYYY_MM_DD_HH_MM_SS);
        String pattern = TimeUtil.YYYY_MM_DD_HH_MM_SS;
        JSONObject config = matrixAttributeVo.getConfig();
        if (MapUtils.isNotEmpty(config)) {
            String format = config.getString("format");
            if (StringUtils.isNotBlank(format)) {
                String styleType = config.getString("styleType");
                if (StringUtils.isNotBlank(styleType) && !Objects.equals(styleType, "-")) {
                    if ("|".equals(styleType)) {
                        styleType = "";
                    }
                    format = format.replace("-", styleType);
                }
                pattern = format;
            }
        }
        System.out.println("pattern = " + pattern);
        SimpleDateFormat patternSdf = new SimpleDateFormat(pattern);
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            try {
                Date date = patternSdf.parse(entry.getKey());
                valueMap.put(entry.getKey(), simpleDateFormat.format(date));
            } catch (ParseException e) {
                // ignore
            }
        }
        return Collections.emptySet();
    }
}
