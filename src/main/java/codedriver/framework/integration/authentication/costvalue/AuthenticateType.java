package codedriver.framework.integration.authentication.costvalue;

public enum AuthenticateType {
	NOAUTH("noauth", "无需认证"), BUILDIN("buildin", "内部验证"), BASIC("basicauth", "Basic认证"), BEARER("bearertoken", "Bearer Token");

	private String type;
	private String text;

	private AuthenticateType(String _type, String _text) {
		this.type = _type;
		this.text = _text;
	}

	public String getValue() {
		return this.type;
	}

	public String getText() {
		return this.text;
	}
}
