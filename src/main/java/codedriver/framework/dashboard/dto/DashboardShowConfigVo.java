package codedriver.framework.dashboard.dto;

import com.alibaba.fastjson.JSONArray;

public class DashboardShowConfigVo {
	private String name;
	private String displayName;
	private JSONArray dataList;

	public DashboardShowConfigVo() {

	}
	public DashboardShowConfigVo(String name, String displayName, JSONArray dataList) {
		this.name = name;
		this.displayName = displayName;
		this.dataList = dataList;
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
	
}
