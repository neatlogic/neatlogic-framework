package codedriver.framework.integration.authentication.costvalue;

public enum BodyType {
	RAW("raw"), X_WWW_FORM_URLENCODED("x-www-form-urlencoded");

	private String type;

	private BodyType(String _type) {
		this.type = _type;
	}

	public String toString() {
		return this.type;
	}
}
