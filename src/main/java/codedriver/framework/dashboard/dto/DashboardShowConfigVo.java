package codedriver.framework.dashboard.dto;

import com.alibaba.fastjson.JSONArray;

import codedriver.framework.common.constvalue.dashboard.DashboardShowConfig;

public class DashboardShowConfigVo {
	private String name;
	private String displayName;
	private JSONArray dataList;
	private String formHandlerType;
	private Boolean isMulti;

	public DashboardShowConfigVo() {

	}
	public DashboardShowConfigVo(DashboardShowConfig dashboardShowConfig, JSONArray dataList) {
		this.name = dashboardShowConfig.getValue();
		this.displayName = dashboardShowConfig.getText();
		this.dataList = dataList;
		this.formHandlerType = dashboardShowConfig.getFormHandlerType();
		this.isMulti = dashboardShowConfig.getIsMulti();
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
	public JSONArray getDataList() {
		return dataList;
	}
	public void setDataList(JSONArray dataList) {
		this.dataList = dataList;
	}
	public String getFormHandlerType() {
		return formHandlerType;
	}
	public void setFormHandlerType(String formHandlerType) {
		this.formHandlerType = formHandlerType;
	}
	public Boolean getIsMulti() {
		return isMulti;
	}
	public void setIsMulti(Boolean isMulti) {
		this.isMulti = isMulti;
	}
	
}
