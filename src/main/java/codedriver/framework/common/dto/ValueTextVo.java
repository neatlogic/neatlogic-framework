package codedriver.framework.common.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class ValueTextVo {
	
	@EntityField(name = "value", type = ApiParamType.STRING)
	private String value;
	@EntityField(name = "text", type = ApiParamType.STRING)
	private String text;

	public ValueTextVo() {
	}

	public ValueTextVo(String value, String text) {
		this.value = value;
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
