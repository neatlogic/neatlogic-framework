package neatlogic.framework.common.constvalue.dashboard;

import neatlogic.framework.common.constvalue.FormHandlerType;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

public enum DashboardShowConfig {
	TYPE("type", new I18n("common.type"),FormHandlerType.RADIO.toString(),false),
	AGGREGATE("aggregate", new I18n("enum.framework.dashboardshowconfig.aggregate"),FormHandlerType.SELECT.toString(),false),
	GROUPFIELD("groupfield", new I18n("enum.framework.dashboardshowconfig.groupfield"),FormHandlerType.SELECT.toString(),false),
	SUBGROUPFIELD("subgroupfield", new I18n("enum.framework.dashboardshowconfig.subgroupfield"),FormHandlerType.SELECT.toString(),false),
	MAXGROUP("maxgroup",new I18n("enum.framework.dashboardshowconfig.maxgroup"),FormHandlerType.SELECT.toString(),false),
	COLOR("color",new I18n("common.color"),FormHandlerType.RADIO.toString(),false),
	REFRESHTIME("refreshtime",new I18n("enum.framework.dashboardshowconfig.refreshtime"),FormHandlerType.SELECT.toString(),false)
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
		return I18nUtils.getMessage(text.toString());
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
