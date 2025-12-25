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
import neatlogic.framework.exception.util.javascript.ValueConNotNullException;
import neatlogic.framework.exception.util.javascript.ValueIsNotEqualException;
import neatlogic.framework.exception.util.javascript.ValueNeedNullException;
import neatlogic.framework.util.javascript.JavascriptResult;
import neatlogic.framework.util.javascript.JavascriptUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class equal {
    private static final Logger logger = LoggerFactory.getLogger(equal.class);


    private static List<String> convertJsonArray(JSONArray list) {
        List<String> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != null) {
                    result.add(list.getString(i).toLowerCase());
                }
            }
        }
        result.sort(Comparator.comparing(String::toLowerCase));
        return result;
    }

    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList, String label, String uuid) {
        JavascriptResult javascriptResult = new JavascriptResult();
        Map<String, JavascriptResult> errorMap = JavascriptUtil.getResultMap();

        if (errorMap != null) {
            errorMap.put(uuid, javascriptResult);
        }
        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");
        if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
            if (dataValueList.size() == conditionValueList.size()) {
                List<String> newDataList = convertJsonArray(dataValueList);
                List<String> newConditionList = convertJsonArray(conditionValueList);
                if (!newDataList.equals(newConditionList)) {
                    ApiRuntimeException error = new ValueIsNotEqualException(prefix, getValue(dataValueList), getValue(conditionValueList));
                    javascriptResult.setError(error);
                    javascriptResult.setResult(false);
                    if (errorMap == null) {
                        logger.warn(error.getMessage());
                    }
                    return false;
                }
                javascriptResult.setResult(true);
                return true;
            } else {
                ApiRuntimeException error = new ValueIsNotEqualException(prefix, getValue(dataValueList), getValue(conditionValueList));
                javascriptResult.setError(error);
                javascriptResult.setResult(false);
                if (errorMap == null) {
                    logger.warn(error.getMessage());
                }
                return false;
            }
        } else {
            if (CollectionUtils.isEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
                ApiRuntimeException error = new ValueConNotNullException(prefix, getValue(conditionValueList));
                javascriptResult.setError(error);
                javascriptResult.setResult(false);
                if (errorMap == null) {
                    logger.warn(error.getMessage());
                }
                return false;
            } else if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isEmpty(conditionValueList)) {
                ApiRuntimeException error = new ValueNeedNullException(prefix);
                javascriptResult.setError(error);
                javascriptResult.setResult(false);
                if (errorMap == null) {
                    logger.warn(error.getMessage());
                }
                return false;
            }
            javascriptResult.setResult(true);
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
