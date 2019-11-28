package codedriver.framework.auth.param;

public enum AuthParamType {
	STRING("string", "字符串"), 
	LONG("long", "长整形"), 
	JSONOBJECT("JSONObject", "json"),
	JSONARRAY("JSONArray", "json数组"),
	IP("ip","ip"),
	EMAIL("email","邮箱"),
	TELEPHONE("telephone","手机号码"),
	NOAUTH("noAuth","无需校验")
	;
	private String name;
	private String text;

	private AuthParamType(String _name, String _text) {
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
		for (AuthParamType s : AuthParamType.values()) {
			if (s.getValue().equals(name)) {
				return s.getText();
			}
		}
		return "";
	}
	
	public static AuthParamType getAuthParamType(String name) {
		for (AuthParamType s : AuthParamType.values()) {
			if (s.getValue().equals(name)) {
				return s;
			}
		}
		return NOAUTH;
	}
}
