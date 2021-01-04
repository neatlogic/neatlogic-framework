package codedriver.framework.common.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class ValueTextVo {
	
	@EntityField(name = "value", type = ApiParamType.STRING)
	private Object value;
	@EntityField(name = "text", type = ApiParamType.STRING)
	private String text;

	public ValueTextVo() {
	}

	public ValueTextVo(Object value, String text) {
		this.value = value;
		this.text = text;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
