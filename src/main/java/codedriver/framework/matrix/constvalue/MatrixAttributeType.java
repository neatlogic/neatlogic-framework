package codedriver.framework.matrix.constvalue;

import java.util.Arrays;
import java.util.List;

import codedriver.framework.common.constvalue.Expression;
import codedriver.framework.common.constvalue.IEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public enum MatrixAttributeType implements IEnum {
	INPUT("input","文本框", Arrays.asList(Expression.EQUAL, Expression.LIKE), Expression.LIKE),
	SELECT("select","下拉框", Arrays.asList(Expression.INCLUDE), Expression.INCLUDE),
	DATE("date","日期", Arrays.asList(Expression.EQUAL, Expression.LESSTHAN,Expression.GREATERTHAN), Expression.EQUAL),
	USER("user","用户", Arrays.asList(Expression.INCLUDE), Expression.INCLUDE),
	TEAM("team","分组", Arrays.asList(Expression.INCLUDE), Expression.INCLUDE),
	ROLE("role","角色", Arrays.asList(Expression.INCLUDE), Expression.INCLUDE);
	
	private String value;
	private String text;
	private List<Expression> expressionList;
	private Expression expression;
	
	private MatrixAttributeType(String value, String text, List<Expression> expressionList, Expression expression) {
		this.value = value;
		this.text = text;
		this.expressionList = expressionList;
		this.expression = expression;
	}
	public String getValue() {
		return value;
	}
	public String getText() {
		return text;
	}
	public List<Expression> getExpressionList() {
		return expressionList;
	}
	public Expression getExpression() {
		return expression;
	}
	
	public static List<Expression> getExpressionList(String _value) {
		for (MatrixAttributeType s : MatrixAttributeType.values()) {
			if (s.getValue().equals(_value)) {
				return s.getExpressionList();
			}
		}
		return null;
	}
	
	public static Expression getExpression(String _value) {
		for (MatrixAttributeType s : MatrixAttributeType.values()) {
			if (s.getValue().equals(_value)) {
				return s.getExpression();
			}
		}
		return null;
	}


	@Override
	public List getValueTextList() {
		JSONArray array = new JSONArray();
		for(MatrixAttributeType type : MatrixAttributeType.values()){
			array.add(new JSONObject(){
				{
					this.put("value",type.getValue());
					this.put("text",type.getText());
				}
			});
		}
		return array;
	}
}
