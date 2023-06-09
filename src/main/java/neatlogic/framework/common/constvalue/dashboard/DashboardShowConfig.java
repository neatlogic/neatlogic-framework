package neatlogic.framework.common.constvalue.dashboard;

import neatlogic.framework.common.constvalue.FormHandlerType;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

public enum DashboardShowConfig {
	TYPE("type", new I18n("类型"),FormHandlerType.RADIO.toString(),false),
	AGGREGATE("aggregate", new I18n("聚合方式"),FormHandlerType.SELECT.toString(),false),
	GROUPFIELD("groupfield", new I18n("分组条件"),FormHandlerType.SELECT.toString(),false),
	SUBGROUPFIELD("subgroupfield", new I18n("二级分组条件"),FormHandlerType.SELECT.toString(),false),
	MAXGROUP("maxgroup",new I18n("最大组数量"),FormHandlerType.SELECT.toString(),false),
	COLOR("color",new I18n("颜色"),FormHandlerType.RADIO.toString(),false),
	REFRESHTIME("refreshtime",new I18n("刷新时间"),FormHandlerType.SELECT.toString(),false)
	;
	
	private String value;
	private I18n text;
	private String formHandlerType;
	private Boolean isMulti;
	private DashboardShowConfig(String value, I18n text,String formHandlerType,Boolean isMulti) {
		this.value = value;
		this.text = text;
		this.formHandlerType = formHandlerType;
		this.isMulti = isMulti;
	}
	public String getValue() {
		return value;
	}
	
	public String getText() {
		return $.t(text.toString());
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
