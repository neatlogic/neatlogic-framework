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
import neatlogic.framework.exception.util.javascript.*;
import neatlogic.framework.util.javascript.JavascriptResult;
import neatlogic.framework.util.javascript.JavascriptUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 区间运算支持数字型、日期型、时间型和日期时间型
 */
public class between {
    private static final Logger logger = LoggerFactory.getLogger(between.class);

    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList, String label, String uuid) {
        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");
        JavascriptResult javascriptResult = new JavascriptResult();
        Map<String, JavascriptResult> errorMap = JavascriptUtil.getResultMap();

        if (errorMap != null) {
            errorMap.put(uuid, javascriptResult);
        }
        if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
            if (dataValueList.size() == conditionValueList.size()) {
                String dataValue = dataValueList.getString(0);
                String conditionValue = conditionValueList.getString(0);
                if (StringUtils.isNotBlank(dataValue) && StringUtils.isNotBlank(conditionValue)) {
                    String[] range = conditionValue.split("~");
                    String valueBefore;
                    String valueAfter;
                    if (range.length == 2) {
                        valueBefore = range[0];
                        valueAfter = range[1];
                        boolean result = compare(dataValue, valueBefore, valueAfter, label, uuid);
                        javascriptResult.setResult(result);
                        return result;
                    } else if (range.length == 1) {
                        valueBefore = range[0];
                        valueAfter = "";
                        boolean result = compare(dataValue, valueBefore, valueAfter, label, uuid);
                        javascriptResult.setResult(result);
                        return result;
                    } else {
                        ApiRuntimeException error = new ConditionIsNullException(prefix);
                        javascriptResult.setError(error);
                        javascriptResult.setResult(false);
                        if (errorMap == null) {
                            logger.warn(error.getMessage());
                        }
                    }
                }
            } else {
                ApiRuntimeException error = new ValueNumberIsNotEqualException(prefix);
                javascriptResult.setError(error);
                javascriptResult.setResult(false);
                if (errorMap == null) {
                    logger.warn(error.getMessage());
                }
                return false;
            }
        }
        javascriptResult.setResult(false);
        return false;
    }

    private static boolean compare(String dataValue, String valueBefore, String valueAfter, String label, String uuid) {
        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");
        Map<String, JavascriptResult> errorMap = JavascriptUtil.getResultMap();
        JavascriptResult javascriptResult = null;
        if (errorMap != null) {
            javascriptResult = errorMap.get(uuid);
        }
        if (isNumber(dataValue)) {
            double transferValue = Double.parseDouble(dataValue);
            double transferValueBefore = Double.MIN_VALUE;
            double transferValueAfter = Double.MAX_VALUE;
            if (StringUtils.isNotBlank(valueBefore)) {
                try {
                    transferValueBefore = Double.parseDouble(valueBefore);
                } catch (Exception ignored) {

                }
            }
            if (StringUtils.isNotBlank(valueAfter)) {
                try {
                    transferValueAfter = Double.parseDouble(valueAfter);
                } catch (Exception ignored) {

                }
            }
            if (!(transferValue >= transferValueBefore && transferValue <= transferValueAfter)) {
                logger.warn(new ValueNotWithinRangeException(prefix, dataValue, valueBefore, valueAfter).getMessage());
                return false;
            }
            return true;
        } else if (isDate(dataValue) || isDateTime(dataValue) || isTime(dataValue)) {
            try {
                String format = "yyyy-MM-dd";
                if (isDateTime(dataValue)) {
                    format = "yyyy-MM-dd HH:mm:ss";
                } else if (isTime(dataValue)) {
                    format = "HH:mm:ss";
                }
                Date transferValue = DateUtils.parseDate(dataValue, format);
                Date transferValueBefore = null;
                Date transferValueAfter = null;
                if (StringUtils.isNotBlank(valueBefore)) {
                    if (isDate(valueBefore) || isDateTime(valueBefore) || isTime(valueBefore)) {
                        try {
                            transferValueBefore = DateUtils.parseDate(valueBefore, format);
                        } catch (Exception ignored) {

                        }
                    } else if (isNumber(valueBefore)) {
                        try {
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(valueBefore));
                            transferValueBefore = cal.getTime();
                        } catch (Exception ignored) {

                        }
                    }
                }
                if (StringUtils.isNotBlank(valueAfter)) {
                    if (isDate(valueAfter) || isDateTime(valueAfter) || isTime(valueAfter)) {
                        try {
                            transferValueAfter = DateUtils.parseDate(valueAfter, format);
                        } catch (Exception ignored) {

                        }
                    } else if (isNumber(valueAfter)) {
                        try {
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(valueAfter));
                            transferValueAfter = cal.getTime();
                        } catch (Exception ignored) {

                        }
                    }
                }
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                if (transferValueBefore != null && transferValueAfter != null) {
                    if (!(transferValue.after(transferValueBefore) && transferValue.before(transferValueAfter))) {
                        ApiRuntimeException error = new ValueNotWithinRangeException(prefix, sdf.format(transferValue), sdf.format(transferValueBefore), sdf.format(transferValueAfter));
                        if (javascriptResult != null) {
                            javascriptResult.setError(error);
                        }
                        logger.warn(error.getMessage());
                        return false;
                    }
                    return true;
                } else if (transferValueBefore != null) {
                    if (!transferValue.after(transferValueBefore)) {
                        ApiRuntimeException error = new ValueNotAfterException(prefix, sdf.format(transferValue), sdf.format(transferValueBefore));
                        if (javascriptResult != null) {
                            javascriptResult.setError(error);
                        }
                        logger.warn(error.getMessage());
                        return false;
                    }
                    return true;
                } else if (transferValueAfter != null) {
                    if (!transferValue.before(transferValueAfter)) {
                        ApiRuntimeException error = new ValueNotBeforeException(prefix, sdf.format(transferValue), sdf.format(transferValueAfter));
                        if (javascriptResult != null) {
                            javascriptResult.setError(error);
                        }
                        logger.warn(error.getMessage());
                        return false;
                    }
                    return true;
                }
            } catch (ParseException ignored) {
            }
        }
        logger.warn(new ValueIsIrregularException(prefix).getMessage());
        return false;
    }

    private static boolean isNumber(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        try {
            new BigDecimal(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private static boolean isValid(String value, String[] patterns) {
        if (StringUtils.isNotBlank(value)) {
            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    LocalDate.parse(value, formatter);
                    return true;
                } catch (Exception ex) {
                }
            }

        }
        return false;
    }

    private static boolean isDate(String value) {
        String[] patterns = {"yyyy-MM-dd", "yyyy-M-dd", "yyyy-M-d", "yyyy-MM-d"};
        return isValid(value, patterns);
    }


    private static boolean isTime(String value) {
        String[] patterns = {"HH:mm:ss", "HH:mm", "HH", "mm:ss"};
        return isValid(value, patterns);
    }

    private static boolean isDateTime(String value) {
        String[] patterns = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH",
                "yyyy-M-dd HH:mm:ss", "yyyy-M-dd HH:mm", "yyyy-M-dd HH",
                "yyyy-M-d HH:mm:ss", "yyyy-M-d HH:mm", "yyyy-M-d HH",
                "yyyy-MM-d HH:mm:ss", "yyyy-MM-d HH:mm", "yyyy-MM-d HH"};
        return isValid(value, patterns);
    }

}
