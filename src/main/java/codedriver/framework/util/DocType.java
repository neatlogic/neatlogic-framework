package codedriver.framework.util;

public enum DocType {

	PDF("pdf"),
	WORD("word"),
	EXCEL("excel");

	private String value;

	public String getValue() {
		return value;
	}

	private DocType(String value) {
		this.value = value;
	}

}
