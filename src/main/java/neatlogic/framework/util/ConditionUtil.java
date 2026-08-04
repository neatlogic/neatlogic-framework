package neatlogic.framework.util;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.ConditionParamContext;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.util.javascript.JavascriptUtil;
import org.apache.commons.collections4.CollectionUtils;

import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.List;

public class ConditionUtil {

    public static boolean predicate(List<String> curentValueList, String expression, List<String> targetValueList) throws ScriptException {
        Expression processExpression = Expression.getProcessExpression(expression);
        if (processExpression == null) {
            //尝试用js脚本引擎进行比对
            ConditionParamContext context = ConditionParamContext.get();
            if (context != null) {
                JSONObject paramData = context.getParamData();
                JSONObject paramObj = new JSONObject();
                paramObj.put("data", paramData);
                Object returnValue = JavascriptUtil.runScript(paramObj, expression);
                return Boolean.parseBoolean(returnValue != null ? returnValue.toString() : "false");
                //JavascriptUtil.runExpression(paramData,)
            }
            return false;
        } else {
            switch (processExpression) {
                case LIKE:
                    if (CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
                        return false;
                    }
                    return String.join("#", curentValueList).contains(String.join("#", targetValueList));
                case NOTLIKE:
                    if (CollectionUtils.isEmpty(targetValueList)) {
                        return false;
                    }
                    if (CollectionUtils.isEmpty(curentValueList)) {
                        return true;
                    }
                    return !String.join("#", curentValueList).contains(String.join("#", targetValueList));
                case EQUAL:
                    if (CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
                        return false;
                    }
                    return String.join("#", curentValueList).equals(String.join("#", targetValueList));
                case UNEQUAL:
                    if (CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
                        return false;
                    }
                    return !String.join("#", curentValueList).equals(String.join("#", targetValueList));
                case INCLUDE:
                    return targetValueList.removeAll(curentValueList);
                case EXCLUDE:
                    return !targetValueList.removeAll(curentValueList);
                case BETWEEN:
                    if (CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
                        return false;
                    }
                    String dataStr = curentValueList.get(0);
                    boolean result = false;
                    String left = targetValueList.get(0);
                    result = compare(dataStr, left) >= 0;
                    if (result && targetValueList.size() == 2) {
                        String right = targetValueList.get(1);
                        result = compare(dataStr, right) <= 0;
                    }
                    return result;
                case GREATERTHAN:
                    if (CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
                        return false;
                    }
                    return compare(curentValueList.get(0), targetValueList.get(0)) > 0;
                case GREATERTHANOREQUAL:
                    if (CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
                        return false;
                    }
                    return compare(curentValueList.get(0), targetValueList.get(0)) >= 0;
                case LESSTHAN:
                    if (CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
                        return false;
                    }
                    return compare(curentValueList.get(0), targetValueList.get(0)) < 0;
                case LESSTHANOREQUAL:
                    if (CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
                        return false;
                    }
                    return compare(curentValueList.get(0), targetValueList.get(0)) <= 0;
                case ISNULL:
                    return CollectionUtils.isEmpty(curentValueList);
                case ISNOTNULL:
                    return CollectionUtils.isNotEmpty(curentValueList);
                default:

                    return false;
            }
        }
    }

    /**
     * 数字条件使用 BigDecimal 比较；非数字值保留原有的长度加字典序比较语义，兼容日期等历史条件。
     */
    private static int compare(String currentValue, String targetValue) {
        if (isDecimal(currentValue) && isDecimal(targetValue)) {
            return new BigDecimal(currentValue).compareTo(new BigDecimal(targetValue));
        }
        int lengthCompare = Integer.compare(currentValue.length(), targetValue.length());
        return lengthCompare != 0 ? lengthCompare : currentValue.compareTo(targetValue);
    }

    private static boolean isDecimal(String value) {
        return value != null && value.matches("[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?");
    }
}
