package codedriver.framework.dto;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.notify.dto.ExpressionVo;

public class ConditionParamVo {
	private String name;
	private String displayName;
	private String controller;
	private Boolean isMultiple = false;
	private JSONObject config;
	private String type;
	private String paramType;
	private String paramTypeName;
	private String defaultExpression;
	private List<ExpressionVo> expressionList = new ArrayList<>();
	private int isEditable = 1;

	public Boolean getIsMultiple() {
		return isMultiple;
	}
	public void setIsMultiple(Boolean isMultiple) {
		this.isMultiple = isMultiple;
	}
	public JSONObject getConfig() {
		return config;
	}
	public void setConfig(String config) {
		try {
			this.config = JSON.parseObject(config);
		}catch(Exception e) {
			
		}
	}
	@JSONField(serialize = false)
	public String getConfigStr() {
		if (config != null) {
			return config.toJSONString();
		}
		return null;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getDefaultExpression() {
		return defaultExpression;
	}
	public void setDefaultExpression(String defaultExpression) {
		this.defaultExpression = defaultExpression;
	}
	public List<ExpressionVo> getExpressionList() {
		return expressionList;
	}
	public void setExpressionList(List<ExpressionVo> expressionList) {
		this.expressionList = expressionList;
	}
	public int getIsEditable() {
		return isEditable;
	}
	public void setIsEditable(int isEditable) {
		this.isEditable = isEditable;
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	public String getParamTypeName() {
		return paramTypeName;
	}
	public void setParamTypeName(String paramTypeName) {
		this.paramTypeName = paramTypeName;
	}
	public void setConfig(JSONObject config) {
		this.config = config;
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
	public String getController() {
		return controller;
	}
	public void setController(String controller) {
		this.controller = controller;
	}
}
