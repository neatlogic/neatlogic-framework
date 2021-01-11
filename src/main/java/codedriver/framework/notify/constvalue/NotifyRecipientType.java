package codedriver.framework.notify.constvalue;


/**
 * 通知接收者类型
 */
public enum NotifyRecipientType {
	USER("user", "用户"),
	TEAM("team", "组"),
	ROLE("role", "角色"),
	EMAIL("email", "邮箱"),
	PROCESSUSERTYPE("processUserType", "工单干系人"),
	CUSTOM("custom", "组件自定义的对象")
	;

	private String value;
	private String text;
	private NotifyRecipientType(String value, String text) {
		this.value = value;
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	
	public String getValuePlugin() {
		return value + "#";
	}

	public String getText() {
		return text;
	}
	
	public static String getValue(String _value) {
		for(NotifyRecipientType type : NotifyRecipientType.values()) {
			if(type.value.equals(_value)) {
				return type.value;
			}
		}
		return null;
	}
	
	public static NotifyRecipientType getNotifyRecipientType(String _value) {
		for(NotifyRecipientType type : NotifyRecipientType.values()) {
			if(type.value.equals(_value)) {
				return type;
			}
		}
		return null;
	}
	
	public static String removePrefix(String _value) {
		for(NotifyRecipientType type : NotifyRecipientType.values()) {
			if(_value.startsWith(type.getValuePlugin())) {
				return _value.substring(type.getValuePlugin().length());
			}
		}
		return _value;
	}
}
