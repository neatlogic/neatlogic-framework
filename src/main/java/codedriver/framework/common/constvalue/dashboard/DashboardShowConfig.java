package codedriver.framework.common.constvalue.dashboard;

public enum DashboardShowConfig {
	AGGREGATE("aggregate", "聚合方式"),
	GROUPFIELD("groupfield", "分组条件"),
	SUBGROUPFIELD("subgroupfield", "二级分组条件"),
	MAXGROUP("maxgroup","最大组数量"),
	REFRESHTIME("refeshtime","刷新时间")
	;
	
	private String value;
	private String text;
	private DashboardShowConfig(String value, String text) {
		this.value = value;
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	
	public String getText() {
		return text;
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
