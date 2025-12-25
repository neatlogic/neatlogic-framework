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
import neatlogic.framework.exception.util.javascript.ConditionIsIrregularException;
import neatlogic.framework.exception.util.javascript.ValueIsIrregularException;
import neatlogic.framework.exception.util.javascript.ValueIsNotGtException;
import neatlogic.framework.util.javascript.JavascriptResult;
import neatlogic.framework.util.javascript.JavascriptUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class gt {

    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList, String label, String uuid) {
        JavascriptResult javascriptResult = new JavascriptResult();
        Map<String, JavascriptResult> errorMap = JavascriptUtil.getResultMap();
        if (errorMap != null) {
            errorMap.put(uuid, javascriptResult);
        }
        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");
        if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
            for (int i = 0; i < dataValueList.size(); i++) {
                Double d;
                try {
                    d = dataValueList.getDouble(i);
                } catch (Exception e) {
                    javascriptResult.setError(new ValueIsIrregularException(prefix));
                    javascriptResult.setResult(false);
                    return false;
                }
                if (d == null) {
                    javascriptResult.setError(new ConditionIsIrregularException(prefix));
                    javascriptResult.setResult(false);
                    return false;
                }
                for (int j = 0; j < conditionValueList.size(); j++) {
                    Double c;
                    try {
                        c = conditionValueList.getDouble(i);
                    } catch (Exception e) {
                        javascriptResult.setError(new ConditionIsIrregularException(prefix));
                        javascriptResult.setResult(false);
                        return false;
                    }
                    if (d <= c) {
                        javascriptResult.setError(new ValueIsNotGtException(prefix, d, c));
                        javascriptResult.setResult(false);
                        return false;
                    }
                }
            }
            javascriptResult.setResult(true);
            return true;
        }
        javascriptResult.setResult(false);
        return false;
    }

}
