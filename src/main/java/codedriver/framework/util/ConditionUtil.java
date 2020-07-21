package codedriver.framework.util;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import codedriver.framework.common.constvalue.Expression;

public class ConditionUtil {

	public static boolean predicate(List<String> curentValueList, String expression, List<String> targetValueList) {
		Expression processExpression = Expression.getProcessExpression(expression);
		if(processExpression == null) {
			return false;
		}
		switch(processExpression) {
			case LIKE: 
				if(CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
					return false;
				}			
				if(curentValueList.size() == 0 && targetValueList.size() == 0) {
					return curentValueList.get(0).contains(targetValueList.get(0));					
				}else {
					String currentValue = String.join("#", curentValueList);
					String targetValue = String.join("#", targetValueList);
					return currentValue.contains(targetValue);
				}
			case NOTLIKE: 
				if(CollectionUtils.isEmpty(targetValueList)) {
					return false;
				}
				if(CollectionUtils.isEmpty(curentValueList)) {
					return true;
				}
				if(curentValueList.size() == 0 && targetValueList.size() == 0) {
					return !curentValueList.get(0).contains(targetValueList.get(0));					
				}else {
					String currentValue = String.join("#", curentValueList);
					String targetValue = String.join("#", targetValueList);
					return !currentValue.contains(targetValue);
				}
			case EQUAL: 
				if(CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
					return false;
				}
				if(curentValueList.size() == 0 && targetValueList.size() == 0) {
					return curentValueList.get(0).equals(targetValueList.get(0));					
				}else {
					String currentValue = String.join("#", curentValueList);
					String targetValue = String.join("#", targetValueList);
					return currentValue.equals(targetValue);
				}
			case UNEQUAL: 
				if(CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
					return false;
				}
				if(curentValueList.size() == 0 && targetValueList.size() == 0) {
					return !curentValueList.get(0).equals(targetValueList.get(0));					
				}else {
					String currentValue = String.join("#", curentValueList);
					String targetValue = String.join("#", targetValueList);
					return !currentValue.equals(targetValue);
				}
			case INCLUDE: 
				return targetValueList.removeAll(curentValueList);
			case EXCLUDE: 
				return !targetValueList.removeAll(curentValueList);
			case BETWEEN: 
				if(CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
					return false;
				}
				String dataStr = curentValueList.get(0);
				boolean result = false;
				String left = targetValueList.get(0);
				if(dataStr.length() > left.length()) {
					result = true;
				}else if(dataStr.length() < left.length()) {
					result = false;
				}else {
					result = dataStr.compareTo(left) >= 0 ? true : false;
				}
				if(result && targetValueList.size() == 2) {
					String right = targetValueList.get(1);
					if(dataStr.length() > right.length()) {
						result = false;
					}else if(dataStr.length() < right.length()) {
						result = true;
					}else {
						result = dataStr.compareTo(right) <= 0 ? true : false;
					}
				}
				return result;
			case GREATERTHAN: 
				if(CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
					return false;
				}
				if(curentValueList.get(0).length() > targetValueList.get(0).length()) {
					return true;
				}else if(curentValueList.get(0).length() < targetValueList.get(0).length()) {
					return false;
				}else {
					return curentValueList.get(0).compareTo(targetValueList.get(0)) > 0 ? true : false;
				}
			case LESSTHAN: 
				if(CollectionUtils.isEmpty(targetValueList) || CollectionUtils.isEmpty(curentValueList)) {
					return false;
				}
				if(curentValueList.get(0).length() > targetValueList.get(0).length()) {
					return false;
				}else if(curentValueList.get(0).length() < targetValueList.get(0).length()) {
					return true;
				}else {
					return curentValueList.get(0).compareTo(targetValueList.get(0)) < 0 ? true : false;
				}
			case ISNULL:
				if(CollectionUtils.isEmpty(curentValueList)) {
					return true;
				}else {
					return false;
				}
			default : 
				return false;
		}
	}
}
