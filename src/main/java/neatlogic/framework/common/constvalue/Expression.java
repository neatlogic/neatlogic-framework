/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.common.constvalue;

import neatlogic.framework.dto.ExpressionVo;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum Expression implements IEnum {
    LIKE("like", new I18n("包含"), " %s contains %s ", " %s.%s like '%s%%' ", 1),
    NOTLIKE("notlike", new I18n("不包含"), " not %s contains %s ", " %s.%s not like '%s%%' ", 1),
    EQUAL("equal", new I18n("等于"), " %s = %s ", " %s.%s = '%s' ", 1),
    UNEQUAL("unequal", new I18n("不等于"), " not %s = %s ", " %s.%s != '%s' ", 1),
    INCLUDE("include", new I18n("包括"), " %s contains any ( %s ) ", " %s.%s in ( '%s' ) ", 1),
    EXCLUDE("exclude", new I18n("不包括"), " not %s contains any ( %s ) ", " %s.%s not in ( '%s' ) ", 1),
    BETWEEN("between", new I18n("属于"), " %s between '%s' and '%s' ", " %s.%s between '%s' and '%s' ", 1),
    GREATERTHAN("greater-than", new I18n("晚于"), " %s > %s ", " %s.%s > '%s' ", 1),
    LESSTHAN("less-than", new I18n("早于"), " %s < %s ", " %s.%s < '%s' ", 1),
    ISNULL("is-null", new I18n("为空"), " %s = '' ", " %s.%s is null ", 0),
    MATCH("match", new I18n("包含(分词)"), " %s match '%s'", " %s.%s match ( %s ) against (' %s ' IN BOOLEAN MODE) ", 0),
    ISNOTNULL("is-not-null", new I18n("不为空"), " not %s = '' ", " %s.%s is not null ", 0);
    private final String expression;
    private final I18n expressionName;
    private final String expressionEs;
    private final String expressionSql;
    private final Integer isShowConditionValue;

    Expression(String _expression, I18n _expressionName, String _expressionEs, String _expressionSql, Integer _isShowConditionValue) {
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
        return $.t(expressionName.toString());
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
        return _expression;
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
