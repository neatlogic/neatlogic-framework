package codedriver.framework.common.dto;

public class ValueTextVo {

	private String value;
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
