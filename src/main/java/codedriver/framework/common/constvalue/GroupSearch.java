package codedriver.framework.common.constvalue;

public enum GroupSearch {
	USER("user", "用户类型"),
	TEAM("team", "组类型"),
	ROLE("role", "角色类型"),
	COMMON("common", "公共类型"),
	;
	
	private String value;
	private String text;
	private GroupSearch(String value, String text) {
		this.value = value;
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	
	public String getValuePlugin() {
		return value+"#";
	}

	public String getText() {
		return text;
	}
	
	public static String getValue(String _value) {
		for(GroupSearch gs : GroupSearch.values()) {
			if(gs.value.equals(_value)) {
				return gs.value;
			}
		}
		return null;
	}
	
	public static GroupSearch getGroupSearch(String _value) {
		for(GroupSearch gs : GroupSearch.values()) {
			if(gs.value.equals(_value)) {
				return gs;
			}
		}
		return null;
	}
}
