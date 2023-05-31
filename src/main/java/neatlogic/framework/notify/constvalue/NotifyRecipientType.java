package neatlogic.framework.notify.constvalue;


import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

/**
 * 通知接收者类型
 */
public enum NotifyRecipientType {
	USER("user", new I18n("common.user")),
	TEAM("team", new I18n("common.group")),
	ROLE("role", new I18n("common.role")),
	EMAIL("email", new I18n("common.email")),
	PROCESSUSERTYPE("processUserType", new I18n("enum.framework.notifyrecipienttype.processusertype")),
	CUSTOM("custom", new I18n("enum.framework.notifyrecipienttype.custom"))
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
		return I18nUtils.getMessage(text.toString());
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
