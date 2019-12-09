package codedriver.framework.apiparam.core;

public enum ApiParamType {
	INTEGER("int", "整型"),
	ENUM("enum", "枚举型"),
	BOOLEAN("boolean", "布尔型"),
	STRING("string", "字符型"),
	LONG("long", "长整型"),
	JSONOBJECT("jsonObject", "json对象"),
	JSONARRAY("jsonArray", "json数组"),
	IP("ip", "ip"),
	EMAIL("email", "邮箱"),
	REGEX("regex", "正则表达式"),
	NOAUTH("noAuth", "无需校验");

	private String name;
	private String text;

	private ApiParamType(String _name, String _text) {
		this.name = _name;
		this.text = _text;
	}

	public String getValue() {
		return name;
	}

	public String getText() {
		return text;
	}

	public static String getText(String name) {
		for (ApiParamType s : ApiParamType.values()) {
			if (s.getValue().equals(name)) {
				return s.getText();
			}
		}
		return "";
	}

	public static ApiParamType getApiParamType(String name) {
		for (ApiParamType s : ApiParamType.values()) {
			if (s.getValue().equals(name)) {
				return s;
			}
		}
		return NOAUTH;
	}
}
