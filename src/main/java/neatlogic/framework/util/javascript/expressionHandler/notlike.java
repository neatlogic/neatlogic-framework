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

package neatlogic.framework.util.javascript.expressionHandler;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.util.javascript.ValueContainException;
import neatlogic.framework.util.javascript.JavascriptUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class notlike {
    private static final Logger logger = LoggerFactory.getLogger(notlike.class);


    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList, String label) {
        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");
        List<ApiRuntimeException> errorList = JavascriptUtil.getErrorList();
        if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
            //单值判断，按照字符串匹配的方式来判断
            if (dataValueList.size() == conditionValueList.size() && dataValueList.size() == 1) {
                String dataValue = dataValueList.getString(0);
                String conditionValue = conditionValueList.getString(0);
                if (!dataValue.contains(conditionValue)) {
                    return true;
                } else {
                    ApiRuntimeException error = new ValueContainException(prefix, dataValue, conditionValue);
                    if (errorList != null) {
                        errorList.add(error);
                    } else {
                        logger.warn(error.getMessage());
                    }
                    return false;
                }
            } else {
                //多值判断，数据中任意成员包含条件任意成员则返回false
                for (int i = 0; i < conditionValueList.size(); i++) {
                    String cValue = conditionValueList.getString(i);
                    if (dataValueList.stream().anyMatch(d -> d.toString().equalsIgnoreCase(cValue))) {
                        ApiRuntimeException error = new ValueContainException(prefix, getValue(dataValueList), getValue(conditionValueList));
                        if (errorList != null) {
                            errorList.add(error);
                        } else {
                            logger.warn(error.getMessage());
                        }
                        return false;
                    }
                }
                return true;
            }
        } else if (CollectionUtils.isEmpty(dataValueList) && CollectionUtils.isEmpty(conditionValueList)) {
            ApiRuntimeException error = new ValueContainException(prefix);
            if (errorList != null) {
                errorList.add(error);
            } else {
                logger.warn(error.getMessage());
            }
            return false;
        } else {
            return true;
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
