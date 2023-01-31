/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.util.javascript.expressionHandler;

import neatlogic.framework.exception.core.ApiRuntimeException;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class like {
    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList, String label) {
        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");

        if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
            if (dataValueList.size() == conditionValueList.size()) {
                //单值判断
                if (dataValueList.size() == 1) {
                    String dataValue = dataValueList.getString(0);
                    String conditionValue = conditionValueList.getString(0);
                    if (dataValue.contains(conditionValue)) {
                        return true;
                    } else {
                        throw new ApiRuntimeException(prefix + "值 " + dataValue + " 不包含 " + conditionValue + "");
                    }
                } else {
                    for (int i = 0; i < conditionValueList.size(); i++) {
                        String cValue = conditionValueList.getString(i);
                        if (dataValueList.stream().noneMatch(d -> d.toString().equals(cValue))) {
                            throw new ApiRuntimeException(prefix + "值 " + getValue(dataValueList) + " 不包含 " + getValue(conditionValueList));
                        }
                    }
                    return true;
                }
            } else if (dataValueList.size() > conditionValueList.size()) {
                for (int i = 0; i < conditionValueList.size(); i++) {
                    String cValue = conditionValueList.getString(i);
                    if (dataValueList.stream().noneMatch(d -> d.toString().equals(cValue))) {
                        throw new ApiRuntimeException(prefix + "值 " + getValue(dataValueList) + " 不包含 " + getValue(conditionValueList));
                    }
                }
                return true;
            } else {
                throw new ApiRuntimeException(prefix + "值 " + getValue(dataValueList) + " 不包含 " + getValue(conditionValueList));
            }
        } else {
            if (CollectionUtils.isEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
                throw new ApiRuntimeException(prefix + "值为空");
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
