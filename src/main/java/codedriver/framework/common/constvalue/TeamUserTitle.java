package codedriver.framework.common.constvalue;

public enum TeamUserTitle {

	DEPARTMENTLEADER("departmentleader", "部门长"),
	TEAMLEADER("teamleader", "组长"),
	CENTERLEADER("centerleader", "中心总监"),
	COMPANYLEADER("companyleader", "公司领导"),
	GROUPLEADER("groupleader", "集团领导");
	private String value;
	private String text;
	private TeamUserTitle(String value, String text) {
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
		for(TeamUserTitle type : values()) {
			if(type.getValue().equals(_value)) {
				return type.getValue();
			}
		}
		return null;
	}
	public static String getText(String _value) {
		for(TeamUserTitle type : values()) {
			if(type.getValue().equals(_value)) {
				return type.getText();
			}
		}
		return "";
	}
}
