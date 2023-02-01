package neatlogic.framework.common.constvalue.integration;

import neatlogic.framework.common.constvalue.ApiParamType;

public enum IntegrationParamType {
	NUMBER("number", "数字"), STRING("string", "字符型"), JSONOBJECT("jsonObject", "json对象"), JSONARRAY("jsonArray", "json数组");

	private String name;
	private String text;

	private IntegrationParamType(String _name, String _text) {
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

}
