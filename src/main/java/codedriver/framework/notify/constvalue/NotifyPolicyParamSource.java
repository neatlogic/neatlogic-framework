package codedriver.framework.notify.constvalue;

public enum NotifyPolicyParamSource {
	SYSTEM("system", "系统"),
	CUSTOM("custom", "自定义");
	private String value;
	private String text;
	private NotifyPolicyParamSource(String value, String text) {
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
