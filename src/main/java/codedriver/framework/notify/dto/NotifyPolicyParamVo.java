package codedriver.framework.notify.dto;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class NotifyPolicyParamVo {

	private String handler;
	private String handlerName;
	private String handlerType;
	private Boolean isMultiple;
	private JSONObject config;
	private String type;
	private String basicType;
	private String basicTypeName;
	private String defaultExpression;
	private List<ExpressionVo> expressionList = new ArrayList<>();
	private int isEditable = 1;
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	public String getHandlerName() {
		return handlerName;
	}
	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
	public String getHandlerType() {
		return handlerType;
	}
	public void setHandlerType(String handlerType) {
		this.handlerType = handlerType;
	}
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
	public String getBasicType() {
		return basicType;
	}
	public void setBasicType(String basicType) {
		this.basicType = basicType;
	}
	public String getBasicTypeName() {
		return basicTypeName;
	}
	public void setBasicTypeName(String basicTypeName) {
		this.basicTypeName = basicTypeName;
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
}
