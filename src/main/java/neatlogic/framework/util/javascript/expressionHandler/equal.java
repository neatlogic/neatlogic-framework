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
