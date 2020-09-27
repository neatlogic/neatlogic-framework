package codedriver.framework.elasticsearch.constvalue;

public enum ESKeyType {
	PKEY("pkey","主键"),KEY("key","普通字段");

	private String value;
	private String text;

	private ESKeyType(String _value, String _text) {
		this.value = _value;
		this.text = _text;
	}

	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}

	public static String getValue(String _status) {
		for (ESKeyType s : ESKeyType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getValue();
			}
		}
		return null;
	}

	public static String getText(String _status) {
		for (ESKeyType s : ESKeyType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getText();
			}
		}
		return "";
	}

}
