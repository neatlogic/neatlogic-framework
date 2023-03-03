package neatlogic.framework.common.constvalue;

/**
 * 导出的文件类型
 */
public enum ExportFileType {
    EXCEL("excel"),
    CSV("csv");
    private String value;

    ExportFileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    public static String getValue(String value) {
        for (ExportFileType type : values()) {
            if (type.getValue().equals(value)) {
                return type.getValue();
            }
        }
        return "";
    }
}
