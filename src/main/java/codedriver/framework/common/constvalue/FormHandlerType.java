package codedriver.framework.common.constvalue;

public enum FormHandlerType {
	INPUT("input"), SELECT("select"), TEXTAREA("textarea"),EDITOR("editor"), RADIO("radio"),CHECKBOX("checkbox"),DATE("date"), TIME("time"),USERSELECT("userselect"),TEAMSELECT("teamselect");
	private String name;

	private FormHandlerType(String _name) {
		this.name = _name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
