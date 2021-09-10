/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util.javascript.expressionHandler;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;

public class equal {
    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList) {
        if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
            if (dataValueList.size() == conditionValueList.size()) {
                dataValueList.sort(Comparator.comparing(Object::toString));
                conditionValueList.sort(Comparator.comparing(Object::toString));
                return dataValueList.toString().equals(conditionValueList.toString());
            } else {
                return false;
            }
        } else {
            return CollectionUtils.isEmpty(dataValueList) && CollectionUtils.isEmpty(conditionValueList);
        }
    }
}
