package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public enum ParamType implements IEnum{
	STRING("string","字符串",Arrays.asList(Expression.EQUAL,Expression.UNEQUAL,Expression.LIKE,Expression.NOTLIKE,Expression.ISNULL,Expression.ISNOTNULL),Expression.LIKE),
	NUMBER("number","数字",Arrays.asList(Expression.EQUAL,Expression.UNEQUAL,Expression.LESSTHAN,Expression.GREATERTHAN,Expression.BETWEEN,Expression.ISNULL,Expression.ISNOTNULL),Expression.EQUAL),
	ENUM("enum","枚举",Arrays.asList(Expression.EQUAL,Expression.UNEQUAL),Expression.EQUAL),
	ARRAY("array","数组",Arrays.asList(Expression.INCLUDE,Expression.EXCLUDE,Expression.ISNULL,Expression.ISNOTNULL),Expression.INCLUDE),
	DATE("date","日期",Arrays.asList(Expression.BETWEEN,Expression.ISNULL,Expression.ISNOTNULL),Expression.BETWEEN)
	;
	private String name;
	private String text;
	private List<Expression> expressionList;
	private Expression defaultExpression;

	private ParamType(String _name,String _text,List<Expression> _expressionList,Expression _defaultExpression) {
		this.name = _name;
		this.text = _text;
		this.expressionList = _expressionList;
		this.defaultExpression = _defaultExpression;
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}

	public List<Expression> getExpressionList() {
		return expressionList;
	}
	
	public JSONArray getExpressionJSONArray() {
		JSONArray expresstionArray = new JSONArray();
		if(CollectionUtils.isNotEmpty(expressionList)) {
			for(Expression express:expressionList) {
				JSONObject expressionObj = new JSONObject();
				expressionObj.put("expression", express.getExpression());
				expressionObj.put("expressionName", express.getExpressionName());
				expresstionArray.add(expressionObj);
			}
		}
		return expresstionArray;
	}

	public Expression getDefaultExpression() {
		return defaultExpression;
	}

	public static ParamType getParamType(String name) {
		for(ParamType type : values()) {
			if(type.getName().equals(name)) {
				return type;
			}
		}
		return null;
	}
	
	public String getFreemarkerTemplate(String name) {
		switch(this) {
			case STRING : 				
				return "${DATA." + name + "}";
			case NUMBER : 
				return "${DATA." + name + "}";
			case ARRAY : 
				return "<#if DATA." + name + "?? && (DATA." + name + "?size > 0)><#list DATA." + name + " as item>${item_index}-${item}<#if item_has_next>,</#if></#list></#if>";
			case DATE : 
				return "${DATA." + name + "}";
			default : break;
		}
		return null;
	}


	@Override
	public List getValueTextList() {
		JSONArray array = new JSONArray();
		for (ParamType type : ParamType.values()) {
			array.add(new JSONObject(){
				{
					this.put("value",type.getName());
					this.put("text",type.getText());
				}
			});
		}
		return array;
	}
}
