package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.$;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public enum ParamType implements IEnum {
    STRING("string", Arrays.asList(Expression.EQUAL, Expression.UNEQUAL, Expression.LIKE, Expression.NOTLIKE, Expression.ISNULL, Expression.ISNOTNULL), Expression.LIKE),
    NUMBER("number", Arrays.asList(Expression.EQUAL, Expression.UNEQUAL, Expression.LESSTHAN, Expression.LESSTHANOREQUAL,
            Expression.GREATERTHAN, Expression.GREATERTHANOREQUAL, Expression.BETWEEN, Expression.ISNULL, Expression.ISNOTNULL), Expression.EQUAL),
    ENUM("enum", Arrays.asList(Expression.EQUAL, Expression.UNEQUAL), Expression.EQUAL),
    ARRAY("array", Arrays.asList(Expression.INCLUDE, Expression.EXCLUDE, Expression.ISNULL, Expression.ISNOTNULL), Expression.INCLUDE),
    DATE("date", Arrays.asList(Expression.BETWEEN, Expression.ISNULL, Expression.ISNOTNULL), Expression.BETWEEN);
    private final String name;
    private final List<Expression> expressionList;
    private final Expression defaultExpression;

    ParamType(String _name, List<Expression> _expressionList, Expression _defaultExpression) {
        this.name = _name;
        this.expressionList = _expressionList;
        this.defaultExpression = _defaultExpression;
    }

    public String getName() {
        return name;
    }

    /** 获取当前语言对应的参数类型名称。 */
    public String getText() {
        return $.t("integration.paramtype." + name);
    }

    public List<Expression> getExpressionList() {
        return expressionList;
    }

    public JSONArray getExpressionJSONArray() {
        JSONArray expressionArray = new JSONArray();
        if (CollectionUtils.isNotEmpty(expressionList)) {
            for (Expression express : expressionList) {
                JSONObject expressionObj = new JSONObject();
                expressionObj.put("expression", express.getExpression());
                expressionObj.put("expressionName", express.getExpressionName());
                expressionArray.add(expressionObj);
            }
        }
        return expressionArray;
    }

    public Expression getDefaultExpression() {
        return defaultExpression;
    }

    public static ParamType getParamType(String name) {
        for (ParamType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getFreemarkerTemplate(String name) {
        switch (this) {
            case STRING:
                return "${DATA." + name + "}";
            case NUMBER:
                return "${DATA." + name + "}";
            case ARRAY:
                return "<#if DATA." + name + "?? && (DATA." + name + "?size > 0)><#list DATA." + name + " as item>${item}<#if item_has_next><br></#if></#list></#if>";
            case DATE:
                return "${DATA." + name + "}";
            default:
                break;
        }
        return null;
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (ParamType type : ParamType.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", type.getName());
                    this.put("text", type.getText());
                }
            });
        }
        return array;
    }
}
