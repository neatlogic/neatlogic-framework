package codedriver.framework.apiparam.core;

public enum ApiParamType {
	INTEGER("int", "整形"), BOOLEAN("boolean", "布尔值"), STRING("string", "字符串"), LONG("long", "长整形"), JSONOBJECT("JSONObject", "json"), JSONARRAY("JSONArray", "json数组"), IP("ip", "ip"), EMAIL("email", "邮箱"), REGEX("regex", "正则表达式"), NOAUTH("noAuth", "无需校验");

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
