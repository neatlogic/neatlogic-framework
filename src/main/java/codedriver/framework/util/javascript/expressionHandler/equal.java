/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util.javascript.expressionHandler;

import codedriver.framework.exception.core.ApiRuntimeException;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;

public class equal {
    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList) {
        if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
            if (dataValueList.size() == conditionValueList.size()) {
                dataValueList.sort(Comparator.comparing(Object::toString));
                conditionValueList.sort(Comparator.comparing(Object::toString));
                if (!dataValueList.toString().equals(conditionValueList.toString())) {
                    throw new ApiRuntimeException("值“" + dataValueList + "”不等于“" + conditionValueList + "”");
                }
                return true;
            } else {
                throw new ApiRuntimeException("值“" + dataValueList + "”不等于“" + conditionValueList + "”");
            }
        } else {
            if (CollectionUtils.isEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
                throw new ApiRuntimeException("值要求是“" + conditionValueList + "”，不能是空值");
            } else if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isEmpty(conditionValueList)) {
                throw new ApiRuntimeException("值要求是空值");
            }
            return true;
        }
    }
}
