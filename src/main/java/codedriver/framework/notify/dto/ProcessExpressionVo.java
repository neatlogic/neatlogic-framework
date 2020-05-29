package codedriver.framework.notify.dto;

import java.io.Serializable;

public class ProcessExpressionVo implements Serializable {

	private static final long serialVersionUID = -2045500057556272026L;
	private String expression;
	private String expressionName;
	private String expressionEs;
	
	public ProcessExpressionVo() {
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
