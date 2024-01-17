/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.util.javascript.expressionHandler;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.util.javascript.ValueContainException;
import neatlogic.framework.exception.util.javascript.ValueIsNullException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class notlike {
    private final static Logger logger = LoggerFactory.getLogger(notlike.class);

    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList, String label) {
        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");
        if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
            if (dataValueList.size() == conditionValueList.size()) {
                //单值判断
                if (dataValueList.size() == 1) {
                    String dataValue = dataValueList.getString(0);
                    String conditionValue = conditionValueList.getString(0);
                    if (!dataValue.contains(conditionValue)) {
                        return true;
                    } else {
                        logger.error(new ValueContainException(prefix, getValue(dataValueList), getValue(conditionValueList)).getMessage());
                        return false;
                    }
                } else {
                    for (int i = 0; i < conditionValueList.size(); i++) {
                        String cValue = conditionValueList.getString(i);
                        if (dataValueList.stream().noneMatch(d -> d.toString().equals(cValue))) {
                            return true;
                        }
                    }
                    logger.error(new ApiRuntimeException(prefix, getValue(dataValueList), getValue(conditionValueList)).getMessage());
                    return false;
                }
            } else if (dataValueList.size() > conditionValueList.size()) {
                for (int i = 0; i < conditionValueList.size(); i++) {
                    String cValue = conditionValueList.getString(i);
                    if (dataValueList.stream().noneMatch(d -> d.toString().equals(cValue))) {
                        return true;
                    }
                }
                logger.error(new ValueContainException(prefix, getValue(dataValueList), getValue(conditionValueList)).getMessage());
                return false;
            } else {
                return true;
            }
        } else {
            if (CollectionUtils.isEmpty(dataValueList) && CollectionUtils.isEmpty(conditionValueList)) {
                logger.error(new ValueIsNullException(prefix).getMessage());
                return false;
            } else {
                return true;
            }
        }
    }

    static String getValue(JSONArray valueList) {
        String s = "";
        if (CollectionUtils.isNotEmpty(valueList)) {
            for (int i = 0; i < valueList.size(); i++) {
                if (StringUtils.isNotBlank(s)) {
                    s += "、";
                }
                s += valueList.getString(i);
            }
        }
        return s;
    }
}
