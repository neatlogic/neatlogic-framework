package codedriver.framework.notify.dto;

import java.util.List;

public class NotifyPolicyParamTypeVo {

	public String handler;
	public String handlerName;
	public String handlerType;
	public Boolean isMultiple;
	public String config;
	public String type;
	public String defaultExpression;
	public List<ProcessExpressionVo> expressionList;
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
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
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
	public List<ProcessExpressionVo> getExpressionList() {
		return expressionList;
	}
	public void setExpressionList(List<ProcessExpressionVo> expressionList) {
		this.expressionList = expressionList;
	}
}
