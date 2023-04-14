package neatlogic.framework.matrix.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

import java.util.Arrays;
import java.util.List;

public enum MatrixAttributeType implements IEnum {
    INPUT("input", new I18n("enum.framework.matrixattributetype.input"), Arrays.asList(Expression.EQUAL, Expression.LIKE), Expression.LIKE),
    SELECT("select", new I18n("enum.framework.matrixattributetype.select"), Arrays.asList(Expression.INCLUDE), Expression.INCLUDE),
    DATE("date", new I18n("enum.framework.matrixattributetype.date"), Arrays.asList(Expression.EQUAL, Expression.LESSTHAN, Expression.GREATERTHAN), Expression.EQUAL),
    USER("user", new I18n("enum.framework.matrixattributetype.user"), Arrays.asList(Expression.INCLUDE), Expression.INCLUDE),
    TEAM("team", new I18n("enum.framework.matrixattributetype.team"), Arrays.asList(Expression.INCLUDE), Expression.INCLUDE),
    ROLE("role", new I18n("enum.framework.matrixattributetype.role"), Arrays.asList(Expression.INCLUDE), Expression.INCLUDE);

    private String value;
    private I18n text;
    private List<Expression> expressionList;
    private Expression expression;

    private MatrixAttributeType(String value, I18n text, List<Expression> expressionList, Expression expression) {
        this.value = value;
        this.text = text;
        this.expressionList = expressionList;
        this.expression = expression;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
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
                }
            });
        }
        return array;
    }
}
