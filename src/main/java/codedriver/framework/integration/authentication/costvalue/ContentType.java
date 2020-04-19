package codedriver.framework.integration.authentication.costvalue;

public enum ContentType {
	RAW("raw"), X_WWW_FORM_URLENCODED("x-www-form-urlencoded");

	private String type;

	private ContentType(String _type) {
		this.type = _type;
	}

	public String toString() {
		return this.type;
	}
}
