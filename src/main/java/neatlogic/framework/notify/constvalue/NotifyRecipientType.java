package neatlogic.framework.notify.constvalue;


import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

/**
 * 通知接收者类型
 */
public enum NotifyRecipientType {
	USER("user", new I18n("用户")),
	TEAM("team", new I18n("组")),
	ROLE("role", new I18n("角色")),
	EMAIL("email", new I18n("邮箱")),
	PROCESSUSERTYPE("processUserType", new I18n("工单干系人")),
	CUSTOM("custom", new I18n("组件自定义的对象"))
	;

	private String value;
	private I18n text;
	private NotifyRecipientType(String value, I18n text) {
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
		return $.t(text.toString());
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
