package codedriver.framework.notify.constvalue;

public enum NotifyPolicyActionType {

	CREATE("create", "创建"),
	UPDATE("update", "修改");
	private String value;
	private String text;
	private NotifyPolicyActionType(String value, String text) {
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
