package neatlogic.framework.util;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.ConditionParamContext;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.util.javascript.JavascriptUtil;
import org.apache.commons.collections4.CollectionUtils;

import javax.script.ScriptException;
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
                return Boolean.parseBoolean(returnValue.toString());
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
                    if (dataStr.length() > left.length()) {
                        result = true;
                    } else if (dataStr.length() < left.length()) {
                        result = false;
                    } else {
                        result = dataStr.compareTo(left) >= 0;
                    }
                    if (result && targetValueList.size() == 2) {
                        String right = targetValueList.get(1);
                        if (dataStr.length() > right.length()) {
                            result = false;
                        } else if (dataStr.length() < right.length()) {
                            result = true;
                        } else {
                            result = dataStr.compareTo(right) <= 0;
                        }
                    }
                    return result;
                case GREATERTHAN:
                    if (CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
                        return false;
                    }
                    if (curentValueList.get(0).length() > targetValueList.get(0).length()) {
                        return true;
                    } else if (curentValueList.get(0).length() < targetValueList.get(0).length()) {
                        return false;
                    } else {
                        return curentValueList.get(0).compareTo(targetValueList.get(0)) > 0;
                    }
                case LESSTHAN:
                    if (CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
                        return false;
                    }
                    if (curentValueList.get(0).length() > targetValueList.get(0).length()) {
                        return false;
                    } else if (curentValueList.get(0).length() < targetValueList.get(0).length()) {
                        return true;
                    } else {
                        return curentValueList.get(0).compareTo(targetValueList.get(0)) < 0;
                    }
                case ISNULL:
                    return CollectionUtils.isEmpty(curentValueList);
                case ISNOTNULL:
                    return CollectionUtils.isNotEmpty(curentValueList);
                default:

                    return false;
            }
        }
    }
}
