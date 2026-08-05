/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.common.constvalue;

import neatlogic.framework.dto.ExpressionVo;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum Expression implements IEnum {
    LIKE("like", new I18n("common.like"), " %s contains %s ", " %s.%s like '%s%%' ", 1),
    NOTLIKE("notlike", new I18n("common.notlike"), " not %s contains %s ", " %s.%s not like '%s%%' ", 1),
    EQUAL("equal", new I18n("common.equal"), " %s = %s ", " %s.%s = '%s' ", 1),
    UNEQUAL("unequal", new I18n("common.unequal"), " not %s = %s ", " %s.%s != '%s' ", 1),
    INCLUDE("include", new I18n("common.include"), " %s contains any ( %s ) ", " %s.%s in ( '%s' ) ", 1),
    EXCLUDE("exclude", new I18n("common.exclude"), " not %s contains any ( %s ) ", " %s.%s not in ( '%s' ) ", 1),
    BETWEEN("between", new I18n("common.between"), " %s between '%s' and '%s' ", " %s.%s between '%s' and '%s' ", 1),
    GREATERTHAN("greater-than", new I18n("common.greaterthan"), " %s > %s ", " %s.%s > '%s' ", 1),
    GREATERTHANOREQUAL("greater-than-or-equal", new I18n("common.greaterthanorequal"), " %s >= %s ", " %s.%s >= '%s' ", 1),
    LESSTHAN("less-than", new I18n("common.lessthan"), " %s < %s ", " %s.%s < '%s' ", 1),
    LESSTHANOREQUAL("less-than-or-equal", new I18n("common.lessthanorequal"), " %s <= %s ", " %s.%s <= '%s' ", 1),
    ISNULL("is-null", new I18n("common.isnull"), " %s = '' ", " %s.%s is null ", 0),
    MATCH("match", new I18n("common.match"), " %s match '%s'", " %s.%s match ( %s ) against (' %s ' IN BOOLEAN MODE) ", 0),
    ISNOTNULL("is-not-null", new I18n("common.isnotnull"), " not %s = '' ", " %s.%s is not null ", 0);
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
