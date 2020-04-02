package codedriver.framework.common.constvalue;

public enum UserType {
	ALL("all","所有人"),LOGIN_USER("loginuser","当前登录人");

	private String status;
	private String text;

	private UserType(String _status, String _text) {
		this.status = _status;
		this.text = _text;
	}

	public String getValue() {
		return status;
	}

	public String getText() {
		return text;
	}

	public static String getValue(String _status) {
		for (UserType s : UserType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getValue();
			}
		}
		return null;
	}

	public static String getText(String _status) {
		for (UserType s : UserType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getText();
			}
		}
		return "";
	}

}
