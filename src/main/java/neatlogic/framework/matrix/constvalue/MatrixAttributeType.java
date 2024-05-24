package neatlogic.framework.matrix.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.util.$;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum MatrixAttributeType implements IMatrixAttributeType {
    INPUT("input", "common.textbox", 1, Arrays.asList(Expression.EQUAL, Expression.LIKE), Expression.LIKE),
    SELECT("select", "common.select", 2,Collections.singletonList(Expression.INCLUDE), Expression.INCLUDE),
    DATE("date", "common.date", 3,Arrays.asList(Expression.EQUAL, Expression.LESSTHAN, Expression.GREATERTHAN), Expression.EQUAL),
    USER("user", "common.user", 4,Collections.singletonList(Expression.INCLUDE), Expression.INCLUDE),
    TEAM("team", "common.group", 5,Collections.singletonList(Expression.INCLUDE), Expression.INCLUDE),
    ROLE("role", "common.role", 6,Collections.singletonList(Expression.INCLUDE), Expression.INCLUDE),
    REGION("region", "common.region", 7,Collections.singletonList(Expression.INCLUDE), Expression.INCLUDE);

    private final String value;
    private final String text;
    private final int sort;
    private final List<Expression> expressionList;
    private final Expression expression;

    private MatrixAttributeType(String value, String text,Integer sort, List<Expression> expressionList, Expression expression) {
        this.value = value;
        this.text = text;
        this.sort = sort;
        this.expressionList = expressionList;
        this.expression = expression;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text);
    }

    public int getSort() {
        return sort;
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
        for (MatrixAttributeType type : MatrixAttributeType.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", type.getValue());
                    this.put("text", type.getText());
                    this.put("sort", type.getSort());
                }
            });
        }
        return array;
    }
}
