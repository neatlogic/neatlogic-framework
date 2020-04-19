package codedriver.framework.integration.authentication.costvalue;

public enum AuthenticateType {
	BASIC("basic"), BEARER("bearer");

	private String type;

	private AuthenticateType(String _type) {
		this.type = _type;
	}

	public String toString() {
		return this.type;
	}
}
