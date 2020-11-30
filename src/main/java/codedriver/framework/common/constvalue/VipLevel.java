package codedriver.framework.common.constvalue;

public enum VipLevel {

	A("0"),
	B("1"),
	C("2"),
	D("3"),
	E("4"),
	F("5");
	private String value;
	private VipLevel(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public static String getValue(String _value) {
		for(VipLevel type : values()) {
			if(type.getValue().equals(_value)) {
				return type.getValue();
			}
		}
		return null;
	}
	
}
