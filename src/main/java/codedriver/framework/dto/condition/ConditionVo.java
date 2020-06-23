package codedriver.framework.dto.condition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.ConditionParamContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.common.constvalue.UserType;
import codedriver.framework.util.ConditionUtil;

public class ConditionVo implements Serializable{
	private static final long serialVersionUID = -776692828809703841L;
	
	private String uuid;
	private String name;
	private String displayName;
	private String type;
	private String handler;
	private JSONObject config;
	private Integer sort;
	private String expression;
	private List<String> valueList;
	
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
			if(values.contains(GroupSearch.COMMON.getValuePlugin() + UserType.LOGIN_USER.getValue())) {
				values = values.replaceAll(GroupSearch.COMMON.getValuePlugin()+UserType.LOGIN_USER.getValue(), GroupSearch.USER.getValuePlugin()+UserContext.get().getUserUuid());
			}
			if(values.startsWith("[") && values.endsWith("]")) {
				this.valueList = JSON.parseArray(values, String.class);
			}else {
				this.valueList = new ArrayList<>();
				this.valueList.add(values);
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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

	public JSONObject getConfig() {
		return config;
	}

	public void setConfig(JSONObject config) {
		this.config = config;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public List<String> getValueList() {
		return valueList;
	}

	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}

	public boolean predicate() {
		Object paramValue = null;
		List<String> curentValueList = new ArrayList<>();
		ConditionParamContext context = ConditionParamContext.get();
		if(context != null) {
			JSONObject paramData = context.getParamData();
			paramValue = paramData.get(this.name);
		}
		if(paramValue != null) {
			String value = paramValue.toString();
			if(value.startsWith("[") && value.endsWith("]")) {
				curentValueList = JSON.parseArray(value, String.class);
			}else {
				curentValueList.add(value);
			}
		}
		List<String> targetValueList = new ArrayList<>();
		for(String value : valueList) {
			targetValueList.add(GroupSearch.removePrefix(value));
		}
		return ConditionUtil.predicate(curentValueList, this.expression, targetValueList);
	}
	
}
