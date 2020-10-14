package codedriver.framework.dto.condition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.ConditionParamContext;
import codedriver.framework.common.constvalue.Expression;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.condition.core.ConditionHandlerFactory;
import codedriver.framework.condition.core.IConditionHandler;
import codedriver.framework.util.ConditionUtil;

public class ConditionVo implements Serializable{
	private static final long serialVersionUID = -776692828809703841L;
	
	private String uuid;
	private String name;
	private String type;
	private String handler;
	private String expression;
	private Object valueList;
	private Boolean result;
	public ConditionVo() {
		super();
	}
	
	public ConditionVo(JSONObject jsonObj) {
		this.uuid = jsonObj.getString("uuid");
		this.name = jsonObj.getString("name");
		this.type = jsonObj.getString("type");
		this.handler = jsonObj.getString("handler");
		this.expression = jsonObj.getString("expression");
		String values = jsonObj.getString("valueList");
		if(StringUtils.isNotBlank(values)) {
			if(values.startsWith("[") && values.endsWith("]")) {
				this.valueList = JSON.parseArray(values);
			}else {
				this.valueList = values;
			}
		}				
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public Object getValueList() {
		return valueList;
	}

	public void setValueList(Object valueList) {
		this.valueList = valueList;
	}

	public boolean predicate() {
		result = false;
		ConditionParamContext context = ConditionParamContext.get();
		if(context != null) {
			List<String> curentValueList = new ArrayList<>();
			JSONObject paramData = context.getParamData();
			Object paramValue = paramData.get(this.name);
			if(paramValue != null) {
				if(paramValue instanceof String) {
					curentValueList.add(GroupSearch.removePrefix((String)paramValue));
				}else if(paramValue instanceof List) {
					List<String> values = JSON.parseArray(JSON.toJSONString(paramValue), String.class);
					for(String value : values) {
						curentValueList.add(GroupSearch.removePrefix(value));
					}
				}
			}
			
			List<String> targetValueList = new ArrayList<>();
			if(valueList != null) {
				if(valueList instanceof String) {
					targetValueList.add(GroupSearch.removePrefix((String)valueList));
				}else if(valueList instanceof List){
					List<String> values = JSON.parseArray(JSON.toJSONString(valueList), String.class);
					for(String value : values) {
						targetValueList.add(GroupSearch.removePrefix(value));
					}
				}
			}
			
			result = ConditionUtil.predicate(curentValueList, this.expression, targetValueList);
			/** 将参数名称、表达式、值的value翻译成对应text，目前条件步骤生成活动时用到**/
			if(context.isTranslate()) {
				if("common".equals(type)) {
					IConditionHandler conditionHandler = ConditionHandlerFactory.getHandler(name);
					if(conditionHandler != null) {
						valueList = conditionHandler.valueConversionText(valueList, null);
						name = conditionHandler.getDisplayName();
					}
				}else if("form".equals(type)) {
					IConditionHandler conditionHandler = ConditionHandlerFactory.getHandler("form");
					if(conditionHandler != null) {
						String formConfig = context.getFormConfig();
						if(StringUtils.isNotBlank(formConfig)) {
							JSONObject configObj = new JSONObject();
							configObj.put("attributeUuid", name);
							configObj.put("formConfig", formConfig);
							valueList = conditionHandler.valueConversionText(valueList, configObj);
							name = configObj.getString("name");
						}
					}
				}
				this.expression = Expression.getExpressionName(this.expression);
			}			
		}
		
		return result;
	}

	public Boolean getResult() {
		return result;
	}
	
}
