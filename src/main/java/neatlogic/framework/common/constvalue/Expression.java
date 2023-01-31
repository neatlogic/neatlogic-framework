/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.common.constvalue;

import neatlogic.framework.dto.ExpressionVo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum Expression implements IEnum {
    LIKE("like", "包含", " %s contains %s ", " %s.%s like '%s%%' ", 1),
    NOTLIKE("notlike", "不包含", " not %s contains %s ", " %s.%s not like '%s%%' ", 1),
    EQUAL("equal", "等于", " %s = %s ", " %s.%s = '%s' ", 1),
    UNEQUAL("unequal", "不等于", " not %s = %s ", " %s.%s != '%s' ", 1),
    INCLUDE("include", "包括", " %s contains any ( %s ) ", " %s.%s in ( '%s' ) ", 1),
    EXCLUDE("exclude", "不包括", " not %s contains any ( %s ) ", " %s.%s not in ( '%s' ) ", 1),
    BETWEEN("between", "属于", " %s between '%s' and '%s' ", " %s.%s between '%s' and '%s' ", 1),
    GREATERTHAN("greater-than", "晚于", " %s > %s ", " %s.%s > '%s' ", 1),
    LESSTHAN("less-than", "早于", " %s < %s ", " %s.%s < '%s' ", 1),
    ISNULL("is-null", "为空", " %s = '' ", " %s.%s is null ", 0),
    MATCH("match", "包含(分词)", " %s match '%s'", " %s.%s match ( %s ) against (' %s ' IN BOOLEAN MODE) ", 0),
    ISNOTNULL("is-not-null", "不为空", " not %s = '' ", " %s.%s is not null ", 0);
    private final String expression;
    private final String expressionName;
    private final String expressionEs;
    private final String expressionSql;
    private final Integer isShowConditionValue;

    Expression(String _expression, String _expressionName, String _expressionEs, String _expressionSql, Integer _isShowConditionValue) {
        this.expression = _expression;
        this.expressionName = _expressionName;
        this.expressionEs = _expressionEs;
        this.expressionSql = _expressionSql;
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

    public String getExpressionSql() {
        return expressionSql;
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

    public static String getExpressionSql(String _expression, String... values) {
//		int valueSize = values.length;
//		List<String> valueList = Arrays.asList(values);
        String expression = StringUtils.EMPTY;
        for (Expression s : Expression.values()) {
            if (s.getExpression().equals(_expression)) {
                expression = s.getExpressionSql();
                break;
            }
        }
        return String.format(expression, values);
    }

    public static Expression getProcessExpression(String _expression) {
        for (Expression s : Expression.values()) {
            if (s.getExpression().equals(_expression)) {
                return s;
            }
        }
        return null;
    }


    @Override
    public List getValueTextList() {
        List<ExpressionVo> array = new ArrayList<>();
        for (Expression expression : Expression.values()) {
            array.add(new ExpressionVo(expression));
        }
        return array;
    }
}
