package codedriver.framework.common.constvalue;

public enum Expression {
	LIKE("like", "包含", " %s contains %s ",1),
	NOTLIKE("notlike", "不包含", " not %s contains %s ",1),
	EQUAL("equal", "等于", " %s = %s ",1),
	UNEQUAL("unequal", "不等于", " not %s = %s ",1),
	INCLUDE("include", "包括", " %s contains any ( %s ) ",1),
	EXCLUDE("exclude", "不包括", " not %s contains any ( %s ) ",1),
	BETWEEN("between","属于"," %s between '%s' and '%s' ",1),
	GREATERTHAN("greater-than", "晚于", " %s > %s ",1),
	LESSTHAN("less-than", "早于", " %s < %s ",1),
	ISNULL("is-null", "为空", " %s = '' ",0),
	MATCH("match","包含(分词)"," %s match '%s'",0),
	ISNOTNULL("is-not-null", "不为空", " not %s = '' ",0);
	private String expression;
	private String expressionName;
	private String expressionEs;
	private Integer isShowConditionValue;
	
	private Expression(String _expression, String _expressionName, String _expressionEs,Integer _isShowConditionValue) {
		this.expression = _expression;
		this.expressionName = _expressionName;
		this.expressionEs = _expressionEs;
		this.isShowConditionValue = _isShowConditionValue;
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

	public Integer getIsShowConditionValue() {
		return isShowConditionValue;
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
