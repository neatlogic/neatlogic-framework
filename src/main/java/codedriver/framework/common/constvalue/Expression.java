package codedriver.framework.common.constvalue;

public enum Expression {
	LIKE("like", "包含", " %s contains %s "),
	NOTLIKE("notlike", "不包含", " not %s contains %s "),
	EQUAL("equal", "等于", " %s = %s "),
	UNEQUAL("unequal", "不等于", " not %s = %s "),
	INCLUDE("include", "包含", " %s contains any ( %s ) "),
	EXCLUDE("exclude", "不包含", " not %s contains any ( %s ) "),
	BETWEEN("between","属于"," %s between '%s' and '%s' "),
	GREATERTHAN("greater-than", "晚于", " %s > %s ) "),
	LESSTHAN("less-than", "早于", " %s < %s ) "),
	ISNULL("is-null", "为空", " not %s = '' ");
	private String expression;
	private String expressionName;
	private String expressionEs;
	
	private Expression(String _expression, String _expressionName, String _expressionEs) {
		this.expression = _expression;
		this.expressionName = _expressionName;
		this.expressionEs = _expressionEs;
	}
	
	public String getExpression() {
		return expression;
	}
	
	public String getExpressionName() {
		return expressionName;
	}
	
	public String getExpressionEs() {
		return expressionEs;
	}

	
	public static String getExpressionName(String _expression) {
		for (Expression s : Expression.values()) {
			if (s.getExpression().equals(_expression)) {
				return s.getExpressionName();
			}
		}
		return null;
	}
	
	public static String getExpressionEs(String _expression) {
		for (Expression s : Expression.values()) {
			if (s.getExpression().equals(_expression)) {
				return s.getExpressionEs();
			}
		}
		return null;
	}
	
	public static Expression getProcessExpression(String _expression) {
		for (Expression s : Expression.values()) {
			if (s.getExpression().equals(_expression)) {
				return s;
			}
		}
		return null;
	}
}
