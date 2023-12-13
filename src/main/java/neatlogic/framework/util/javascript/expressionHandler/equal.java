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
import neatlogic.framework.exception.util.javascript.ValueConNotNullException;
import neatlogic.framework.exception.util.javascript.ValueIsNotEqualException;
import neatlogic.framework.exception.util.javascript.ValueNeedNullException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class equal {
    private final static Logger logger = LoggerFactory.getLogger(equal.class);

    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList, String label) {
        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");
        if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
            if (dataValueList.size() == conditionValueList.size()) {
                dataValueList.sort(Comparator.comparing(Object::toString));
                conditionValueList.sort(Comparator.comparing(Object::toString));
                if (!dataValueList.toString().equals(conditionValueList.toString())) {
                    logger.error(new ValueIsNotEqualException(prefix, getValue(dataValueList), getValue(conditionValueList)).getMessage());
                    return false;
                }
                return true;
            } else {
                logger.error(new ValueIsNotEqualException(prefix, getValue(dataValueList), getValue(conditionValueList)).getMessage());
                return false;
            }
        } else {
            if (CollectionUtils.isEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
                logger.error(new ValueConNotNullException(prefix, getValue(conditionValueList)).getMessage());
                return false;
            } else if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isEmpty(conditionValueList)) {
                logger.error(new ValueNeedNullException(prefix).getMessage());
                return false;
            }
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
