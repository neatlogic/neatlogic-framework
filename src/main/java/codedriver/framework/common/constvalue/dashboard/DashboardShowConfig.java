package codedriver.framework.common.constvalue.dashboard;

import codedriver.framework.common.constvalue.FormHandlerType;

public enum DashboardShowConfig {
	TYPE("type", "类型",FormHandlerType.RADIO.toString(),false),
	AGGREGATE("aggregate", "聚合方式",FormHandlerType.SELECT.toString(),false),
	GROUPFIELD("groupfield", "分组条件",FormHandlerType.SELECT.toString(),false),
	SUBGROUPFIELD("subgroupfield", "二级分组条件",FormHandlerType.SELECT.toString(),false),
	MAXGROUP("maxgroup","最大组数量",FormHandlerType.SELECT.toString(),false),
	COLOR("color","颜色",FormHandlerType.RADIO.toString(),false),
	REFRESHTIME("refeshtime","刷新时间",FormHandlerType.SELECT.toString(),false)
	;
	
	private String value;
	private String text;
	private String formHandlerType;
	private Boolean isMulti;
	private DashboardShowConfig(String value, String text,String formHandlerType,Boolean isMulti) {
		this.value = value;
		this.text = text;
		this.formHandlerType = formHandlerType;
		this.isMulti = isMulti;
	}
	public String getValue() {
		return value;
	}
	
	public String getText() {
		return text;
	}
	
	public String getFormHandlerType() {
		return formHandlerType;
	}
	
	public Boolean getIsMulti() {
		return isMulti;
	}
	public static String getValue(String _value) {
		for(DashboardShowConfig gs : DashboardShowConfig.values()) {
			if(gs.value.equals(_value)) {
				return gs.value;
			}
		}
		return null;
	}
	
	public static DashboardShowConfig getGroupSearch(String _value) {
		for(DashboardShowConfig gs : DashboardShowConfig.values()) {
			if(gs.value.equals(_value)) {
				return gs;
			}
		}
		return null;
	}
}
