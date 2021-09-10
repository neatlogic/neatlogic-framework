/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util.javascript.expressionHandler;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;

public class isnotnull {
    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList) {
        return CollectionUtils.isNotEmpty(dataValueList);
    }
}
