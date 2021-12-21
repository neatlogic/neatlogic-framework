package codedriver.framework.common.constvalue;

public enum DeviceType {
	ALL("all","所有"),MOBILE("mobile","手机端"),PC("pc","电脑端");

	private String status;
	private String text;

	private DeviceType(String _status, String _text) {
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
		for (DeviceType s : DeviceType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getValue();
			}
		}
		return null;
	}

	public static String getText(String _status) {
		for (DeviceType s : DeviceType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getText();
			}
		}
		return "";
	}

}
