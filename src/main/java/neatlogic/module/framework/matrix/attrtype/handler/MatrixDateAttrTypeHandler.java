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
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.core.MatrixAttrTypeBase;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MatrixDateAttrTypeHandler extends MatrixAttrTypeBase {
    @Override
    public String getHandler() {
        return MatrixAttributeType.DATE.getValue();
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
        valueMap.replaceAll((k, v) -> k);
        return Collections.emptySet();
    }
}
