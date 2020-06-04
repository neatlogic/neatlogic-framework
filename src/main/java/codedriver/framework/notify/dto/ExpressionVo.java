package codedriver.framework.notify.dto;

import java.io.Serializable;

import codedriver.framework.common.constvalue.Expression;

public class ExpressionVo implements Serializable {

	private static final long serialVersionUID = -2045500057556272026L;
	private String expression;
	private String expressionName;
	private String expressionEs;
	
	public ExpressionVo() {
	}

	public ExpressionVo(Expression expression) {
		this.expression = expression.getExpression();
		this.expressionName = expression.getExpressionName();
		this.expressionEs = expression.getExpressionEs();
	}

	public ExpressionVo(String expression, String expressionName) {
		this.expression = expression;
		this.expressionName = expressionName;
	}
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getExpressionName() {
		return expressionName;
	}
	public void setExpressionName(String expressionName) {
		this.expressionName = expressionName;
	}
	public String getExpressionEs() {
		return expressionEs;
	}
	public void setExpressionEs(String expressionEs) {
		this.expressionEs = expressionEs;
	}
	
}
