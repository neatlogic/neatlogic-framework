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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 区间运算支持数字型、日期型、时间型和日期时间型
 */
public class between {
    private static final Logger logger = LoggerFactory.getLogger(between.class);

    private static final String[] DATE_PATTERNS = {"yyyy-MM-dd", "yyyy-M-dd", "yyyy-M-d", "yyyy-MM-d"};
    private static final String[] TIME_PATTERNS = {"HH:mm:ss", "HH:mm", "HH", "mm:ss"};
    private static final String[] DATETIME_PATTERNS = {
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH",
            "yyyy-M-dd HH:mm:ss", "yyyy-M-dd HH:mm", "yyyy-M-dd HH",
            "yyyy-M-d HH:mm:ss", "yyyy-M-d HH:mm", "yyyy-M-d HH",
            "yyyy-MM-d HH:mm:ss", "yyyy-MM-d HH:mm", "yyyy-MM-d HH"
    };

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
        }

        if (isDateTime(dataValue)) {
            LocalDateTime transferValue = parseDateTimeValue(dataValue);
            if (transferValue == null) {
                logger.warn(new ValueIsIrregularException(prefix).getMessage());
                return false;
            }
            LocalDateTime transferValueBefore = parseDateTimeBoundary(valueBefore, true);
            LocalDateTime transferValueAfter = parseDateTimeBoundary(valueAfter, false);
            String formattedValue = formatDateTime(transferValue);
            if (transferValueBefore != null && transferValueAfter != null) {
                if (!(transferValue.isAfter(transferValueBefore) && transferValue.isBefore(transferValueAfter))) {
                    ApiRuntimeException error = new ValueNotWithinRangeException(prefix, formattedValue, formatDateTime(transferValueBefore), formatDateTime(transferValueAfter));
                    if (javascriptResult != null) {
                        javascriptResult.setError(error);
                    }
                    logger.warn(error.getMessage());
                    return false;
                }
                return true;
            } else if (transferValueBefore != null) {
                if (!transferValue.isAfter(transferValueBefore)) {
                    ApiRuntimeException error = new ValueNotAfterException(prefix, formattedValue, formatDateTime(transferValueBefore));
                    if (javascriptResult != null) {
                        javascriptResult.setError(error);
                    }
                    logger.warn(error.getMessage());
                    return false;
                }
                return true;
            } else if (transferValueAfter != null) {
                if (!transferValue.isBefore(transferValueAfter)) {
                    ApiRuntimeException error = new ValueNotBeforeException(prefix, formattedValue, formatDateTime(transferValueAfter));
                    if (javascriptResult != null) {
                        javascriptResult.setError(error);
                    }
                    logger.warn(error.getMessage());
                    return false;
                }
                return true;
            }
        } else if (isDate(dataValue)) {
            LocalDate transferValue = parseDateValue(dataValue);
            if (transferValue == null) {
                logger.warn(new ValueIsIrregularException(prefix).getMessage());
                return false;
            }
            LocalDate transferValueBefore = parseDateBoundary(valueBefore, true);
            LocalDate transferValueAfter = parseDateBoundary(valueAfter, false);
            String formattedValue = formatDate(transferValue);
            if (transferValueBefore != null && transferValueAfter != null) {
                if (!(transferValue.isAfter(transferValueBefore) && transferValue.isBefore(transferValueAfter))) {
                    ApiRuntimeException error = new ValueNotWithinRangeException(prefix, formattedValue, formatDate(transferValueBefore), formatDate(transferValueAfter));
                    if (javascriptResult != null) {
                        javascriptResult.setError(error);
                    }
                    logger.warn(error.getMessage());
                    return false;
                }
                return true;
            } else if (transferValueBefore != null) {
                if (!transferValue.isAfter(transferValueBefore)) {
                    ApiRuntimeException error = new ValueNotAfterException(prefix, formattedValue, formatDate(transferValueBefore));
                    if (javascriptResult != null) {
                        javascriptResult.setError(error);
                    }
                    logger.warn(error.getMessage());
                    return false;
                }
                return true;
            } else if (transferValueAfter != null) {
                if (!transferValue.isBefore(transferValueAfter)) {
                    ApiRuntimeException error = new ValueNotBeforeException(prefix, formattedValue, formatDate(transferValueAfter));
                    if (javascriptResult != null) {
                        javascriptResult.setError(error);
                    }
                    logger.warn(error.getMessage());
                    return false;
                }
                return true;
            }
        } else if (isTime(dataValue)) {
            LocalTime transferValue = parseTimeValue(dataValue);
            if (transferValue == null) {
                logger.warn(new ValueIsIrregularException(prefix).getMessage());
                return false;
            }
            LocalTime transferValueBefore = parseTimeValue(valueBefore);
            LocalTime transferValueAfter = parseTimeValue(valueAfter);
            String formattedValue = formatTime(transferValue);
            if (transferValueBefore != null && transferValueAfter != null) {
                if (!(transferValue.isAfter(transferValueBefore) && transferValue.isBefore(transferValueAfter))) {
                    ApiRuntimeException error = new ValueNotWithinRangeException(prefix, formattedValue, formatTime(transferValueBefore), formatTime(transferValueAfter));
                    if (javascriptResult != null) {
                        javascriptResult.setError(error);
                    }
                    logger.warn(error.getMessage());
                    return false;
                }
                return true;
            } else if (transferValueBefore != null) {
                if (!transferValue.isAfter(transferValueBefore)) {
                    ApiRuntimeException error = new ValueNotAfterException(prefix, formattedValue, formatTime(transferValueBefore));
                    if (javascriptResult != null) {
                        javascriptResult.setError(error);
                    }
                    logger.warn(error.getMessage());
                    return false;
                }
                return true;
            } else if (transferValueAfter != null) {
                if (!transferValue.isBefore(transferValueAfter)) {
                    ApiRuntimeException error = new ValueNotBeforeException(prefix, formattedValue, formatTime(transferValueAfter));
                    if (javascriptResult != null) {
                        javascriptResult.setError(error);
                    }
                    logger.warn(error.getMessage());
                    return false;
                }
                return true;
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

    private static LocalDate parseDate(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (String pattern : DATE_PATTERNS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDate.parse(value, formatter);
            } catch (Exception ignored) {

            }
        }
        return null;
    }

    private static LocalTime parseTime(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (String pattern : TIME_PATTERNS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalTime.parse(value, formatter);
            } catch (Exception ignored) {

            }
        }
        return null;
    }

    private static LocalDateTime parseDateTime(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (String pattern : DATETIME_PATTERNS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDateTime.parse(value, formatter);
            } catch (Exception ignored) {

            }
        }
        return null;
    }

    private static LocalDate parseDateValue(String value) {
        LocalDate date = parseDate(value);
        if (date != null) {
            return date;
        }
        LocalDateTime dateTime = parseDateTime(value);
        if (dateTime != null) {
            return dateTime.toLocalDate();
        }
        return null;
    }

    private static LocalTime parseTimeValue(String value) {
        LocalTime time = parseTime(value);
        if (time != null) {
            return time;
        }
        LocalDateTime dateTime = parseDateTime(value);
        if (dateTime != null) {
            return dateTime.toLocalTime();
        }
        return null;
    }

    private static LocalDateTime parseDateTimeValue(String value) {
        LocalDateTime dateTime = parseDateTime(value);
        if (dateTime != null) {
            return dateTime;
        }
        LocalDate date = parseDate(value);
        if (date != null) {
            return date.atStartOfDay();
        }
        return null;
    }

    private static LocalDate parseDateBoundary(String value, boolean before) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        LocalDate parsed = parseDateValue(value);
        if (parsed != null) {
            return parsed;
        }
        if (isNumber(value)) {
            try {
                int days = Integer.parseInt(value);
                return before ? LocalDate.now().minusDays(days) : LocalDate.now().plusDays(days);
            } catch (Exception ignored) {

            }
        }
        return null;
    }

    private static LocalDateTime parseDateTimeBoundary(String value, boolean before) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        LocalDateTime parsed = parseDateTimeValue(value);
        if (parsed != null) {
            return parsed;
        }
        if (isNumber(value)) {
            try {
                int days = Integer.parseInt(value);
                return before ? LocalDateTime.now().minusDays(days) : LocalDateTime.now().plusDays(days);
            } catch (Exception ignored) {

            }
        }
        return null;
    }

    private static String formatDate(LocalDate date) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date);
    }

    private static String formatTime(LocalTime time) {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(time);
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
    }

    private static boolean isDate(String value) {
        return parseDate(value) != null;
    }

    private static boolean isTime(String value) {
        return parseTime(value) != null;
    }

    private static boolean isDateTime(String value) {
        return parseDateTime(value) != null;
    }
}
