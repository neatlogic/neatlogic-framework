package codedriver.framework.common.constvalue;

public enum GroupSearch {
	USER("user", "用户类型"),
	TEAM("team", "组类型"),
	ROLE("role", "角色类型");
	
	private String value;
	private String text;
	private GroupSearch(String value, String text) {
		this.value = value;
		this.text = text;
	}
	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}
}
