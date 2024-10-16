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
import neatlogic.framework.exception.util.javascript.*;
import neatlogic.framework.util.javascript.JavascriptUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 区间运算支持数字型、日期型、时间型和日期时间型
 */
public class between {
    private static final Logger logger = LoggerFactory.getLogger(between.class);

    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList, String label) {
        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");
        List<ApiRuntimeException> errorList = JavascriptUtil.getErrorList();
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
                        return compare(dataValue, valueBefore, valueAfter, label);
                    }
                }
            } else {
                ApiRuntimeException error = new ValueNumberIsNotEqualException(prefix);
                if (errorList != null) {
                    errorList.add(error);
                } else {
                    logger.error(error.getMessage());
                }
                return false;
            }
        }
        ApiRuntimeException error = new ValueIsNullException(prefix);
        if (errorList != null) {
            errorList.add(error);
        }
        logger.error(error.getMessage());
        return false;
    }

    private static boolean compare(String dataValue, String valueBefore, String valueAfter, String label) {
        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");
        List<ApiRuntimeException> errorList = JavascriptUtil.getErrorList();
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
                logger.error(new ValueNotWithinRangeException(prefix, dataValue, valueBefore, valueAfter).getMessage());
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
                    try {
                        transferValueBefore = DateUtils.parseDate(valueBefore, format);
                    } catch (Exception ignored) {

                    }
                }
                if (StringUtils.isNotBlank(valueAfter)) {
                    try {
                        transferValueAfter = DateUtils.parseDate(valueAfter, format);
                    } catch (Exception ignored) {

                    }
                }
                if (transferValueBefore != null && transferValueAfter != null) {
                    if (!(transferValue.after(transferValueBefore) && transferValue.before(transferValueAfter))) {
                        ApiRuntimeException error = new ValueNotWithinRangeException(prefix, dataValue, valueBefore, valueAfter);
                        if (errorList != null) {
                            errorList.add(error);
                        }
                        logger.error(error.getMessage());
                        return false;
                    }
                    return true;
                } else if (transferValueBefore != null) {
                    if (!transferValue.after(transferValueBefore)) {
                        ApiRuntimeException error = new ValueNotAfterException(prefix, dataValue, valueBefore);
                        if (errorList != null) {
                            errorList.add(error);
                        }
                        logger.error(error.getMessage());
                        return false;
                    }
                    return true;
                } else if (transferValueAfter != null) {
                    if (!transferValue.before(transferValueAfter)) {
                        ApiRuntimeException error = new ValueNotBeforeException(prefix, dataValue, valueAfter);
                        if (errorList != null) {
                            errorList.add(error);
                        }
                        logger.error(error.getMessage());
                        return false;
                    }
                    return true;
                }
            } catch (ParseException ignored) {
            }
        }
        logger.error(new ValueIsIrregularException(prefix).getMessage());
        return false;
    }

    private static final Set<Character> numberCharSet = new HashSet<>();

    static {
        numberCharSet.add('0');
        numberCharSet.add('1');
        numberCharSet.add('2');
        numberCharSet.add('3');
        numberCharSet.add('4');
        numberCharSet.add('5');
        numberCharSet.add('6');
        numberCharSet.add('7');
        numberCharSet.add('8');
        numberCharSet.add('9');
        numberCharSet.add('.');
    }

    private static boolean isNumber(String value) {
        if (StringUtils.isNotBlank(value)) {
            for (char c : value.toCharArray()) {
                if (!numberCharSet.contains(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean isDate(String value) {
        if (StringUtils.isNotBlank(value)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                sdf.format(value);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }


    private static boolean isTime(String value) {
        if (StringUtils.isNotBlank(value)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            try {
                sdf.format(value);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

    private static boolean isDateTime(String value) {
        if (StringUtils.isNotBlank(value)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                sdf.format(value);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

}
