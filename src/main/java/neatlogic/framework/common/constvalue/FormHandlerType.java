package neatlogic.framework.common.constvalue;

public enum FormHandlerType {
	INPUT("input"), 
	SELECT("select"), 
	TEXTAREA("textarea"),
	EDITOR("editor"), 
	RADIO("radio"),
	CHECKBOX("checkbox"),
	DATE("date"), 
	DATETIME("datetime"),
	TIME("time"),
	TIMESELECT("timeselect"),
	USERSELECT("userselect"),
	TEAMSELECT("teamselect"),
	CASCADELIST("cascadelist"),
	TREE("tree");
	private String name;

	private FormHandlerType(String _name) {
		this.name = _name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
