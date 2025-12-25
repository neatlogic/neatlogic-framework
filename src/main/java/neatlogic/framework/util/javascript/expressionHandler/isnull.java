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
import neatlogic.framework.exception.util.javascript.ValueNeedNullException;
import neatlogic.framework.util.javascript.JavascriptResult;
import neatlogic.framework.util.javascript.JavascriptUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class isnull {
    private static final Logger logger = LoggerFactory.getLogger(isnull.class);

    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList, String label, String uuid) {
        JavascriptResult javascriptResult = new JavascriptResult();
        Map<String, JavascriptResult> errorMap = JavascriptUtil.getResultMap();
        if (errorMap != null) {
            errorMap.put(uuid, javascriptResult);
        }

        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");

        if (CollectionUtils.isNotEmpty(dataValueList)) {
            boolean hasValue = false;
            for (int i = 0; i < dataValueList.size(); i++) {
                String v = dataValueList.getString(i);
                if (StringUtils.isNotBlank(v)) {
                    hasValue = true;
                    break;
                }
            }
            if (hasValue) {
                ApiRuntimeException error = new ValueNeedNullException(prefix);
                javascriptResult.setError(error);
                javascriptResult.setResult(false);
                if (errorMap == null) {
                    logger.warn(error.getMessage());
                }
            }
            javascriptResult.setResult(!hasValue);
            return !hasValue;
        }
        javascriptResult.setResult(true);
        return true;
    }
}
