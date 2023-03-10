package neatlogic.framework.dto;

import java.io.Serializable;

import neatlogic.framework.common.constvalue.Expression;

public class ExpressionVo implements Serializable {

    private static final long serialVersionUID = -2045500057556272026L;
    private String expression;
    private String expressionName;
    private String expressionEs;
    private Integer isShowConditionValue;

    public ExpressionVo() {}

    public ExpressionVo(Expression processExpression) {
        this.expression = processExpression.getExpression();
        this.expressionName = processExpression.getExpressionName();
        this.expressionEs = processExpression.getExpressionEs();
        this.isShowConditionValue = processExpression.getIsShowConditionValue();
    }

    public ExpressionVo(ExpressionVo expressionVo) {
        this.expression = expressionVo.expression;
        this.expressionName = expressionVo.expressionName;
        this.expressionEs = expressionVo.expressionEs;
        this.isShowConditionValue = expressionVo.isShowConditionValue;
    }

    public ExpressionVo(String expression, String expressionName) {
        this.expression = expression;
        this.expressionName = expressionName;
    }

    public ExpressionVo(String expression, String expressionName, Integer isShowConditionValue) {
        this.expression = expression;
        this.expressionName = expressionName;
        this.isShowConditionValue = isShowConditionValue;
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

    public Integer getIsShowConditionValue() {
        return isShowConditionValue;
    }

    public void setIsShowConditionValue(Integer isShowConditionValue) {
        this.isShowConditionValue = isShowConditionValue;
    }

}
