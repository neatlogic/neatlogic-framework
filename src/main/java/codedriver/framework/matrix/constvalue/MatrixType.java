package codedriver.framework.matrix.constvalue;

public enum MatrixType {
    CUSTOM("custom", "自定义数据源"), EXTERNAL("external", "外部数据源");

    private String value;
    private String name;

    private MatrixType(String _value, String _name) {
        this.value = _value;
        this.name = _name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static String getValue(String _value) {
        for (MatrixType s : MatrixType.values()) {
            if (s.getValue().equals(_value)) {
                return s.getValue();
            }
        }
        return null;
    }

    public static String getName(String _value) {
        for (MatrixType s : MatrixType.values()) {
            if (s.getValue().equals(_value)) {
                return s.getName();
            }
        }
        return "";
    }
}