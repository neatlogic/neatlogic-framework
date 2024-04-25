/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.util.javascript.expressionHandler;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.util.javascript.ValueContainException;
import neatlogic.framework.exception.util.javascript.ValueIsNullException;
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
            if (dataValueList.size() == conditionValueList.size()) {
                //单值判断
                if (dataValueList.size() == 1) {
                    String dataValue = dataValueList.getString(0);
                    String conditionValue = conditionValueList.getString(0);
                    if (!dataValue.contains(conditionValue)) {
                        return true;
                    } else {
                        ApiRuntimeException error = new ValueContainException(prefix, getValue(dataValueList), getValue(conditionValueList));
                        if (errorList != null) {
                            errorList.add(error);
                        }
                        logger.error(error.getMessage());
                        return false;
                    }
                } else {
                    for (int i = 0; i < conditionValueList.size(); i++) {
                        String cValue = conditionValueList.getString(i);
                        if (dataValueList.stream().noneMatch(d -> d.toString().equals(cValue))) {
                            return true;
                        }
                    }
                    ApiRuntimeException error = new ApiRuntimeException(prefix, getValue(dataValueList), getValue(conditionValueList));
                    if (errorList != null) {
                        errorList.add(error);
                    }
                    logger.error(error.getMessage());
                    return false;
                }
            } else if (dataValueList.size() > conditionValueList.size()) {
                for (int i = 0; i < conditionValueList.size(); i++) {
                    String cValue = conditionValueList.getString(i);
                    if (dataValueList.stream().noneMatch(d -> d.toString().equals(cValue))) {
                        return true;
                    }
                }
                ApiRuntimeException error = new ValueContainException(prefix, getValue(dataValueList), getValue(conditionValueList));
                if (errorList != null) {
                    errorList.add(error);
                }
                logger.error(error.getMessage());
                return false;
            } else {
                return true;
            }
        } else {
            if (CollectionUtils.isEmpty(dataValueList) && CollectionUtils.isEmpty(conditionValueList)) {
                ApiRuntimeException error = new ValueIsNullException(prefix);
                if (errorList != null) {
                    errorList.add(error);
                }
                logger.error(error.getMessage());
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
