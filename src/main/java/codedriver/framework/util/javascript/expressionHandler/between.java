/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util.javascript.expressionHandler;

import codedriver.framework.exception.core.ApiRuntimeException;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 区间运算支持数字型、日期型、时间型和日期时间型
 */
public class between {

    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList) {
        if (CollectionUtils.isNotEmpty(dataValueList) && CollectionUtils.isNotEmpty(conditionValueList)) {
            if (dataValueList.size() == conditionValueList.size()) {
                String dataValue = dataValueList.getString(0);
                String conditionValue = conditionValueList.getString(0);
                if (StringUtils.isNotBlank(dataValue) && StringUtils.isNotBlank(conditionValue)) {
                    String[] range = conditionValue.split("~");
                    String valueBefore = "", valueAfter = "";
                    if (range.length == 2) {
                        valueBefore = range[0];
                        valueAfter = range[1];
                        return compare(dataValue, valueBefore, valueAfter);
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

    private static boolean compare(String dataValue, String valueBefore, String valueAfter) {
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
                throw new ApiRuntimeException("值" + dataValue + "不在" + valueBefore + "和" + valueAfter + "范围内");
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
                        throw new ApiRuntimeException("值“" + dataValue + "”不在“" + valueBefore + "”和“" + valueAfter + "”范围内");
                    }
                    return true;
                } else if (transferValueBefore != null) {
                    if (!transferValue.after(transferValueBefore)) {
                        throw new ApiRuntimeException("值“" + dataValue + "”不在“" + valueBefore + "”之后");
                    }
                    return true;
                } else if (transferValueAfter != null) {
                    if (!transferValue.before(transferValueAfter)) {
                        throw new ApiRuntimeException("值“" + dataValue + "”不在“" + valueAfter + "”之前");
                    }
                    return true;
                }
            } catch (ParseException ignored) {
            }
        }
        return false;
    }

    private final static Set<Character> numberCharSet = new HashSet<>();

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

    public static void main(String[] arg) {
        String a = "~2";
        System.out.println(a.split("~")[0]);
    }

}
