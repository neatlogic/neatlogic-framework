package codedriver.framework.common.constvalue;

public enum MimeType {
    TXT("text/plain", ".txt"),
    HTML("text/html", ".html"),
    JPG("image/jpeg", ".jpg"),
    GIF("image/gif", ".gif"),
    STREAM("application/octet-stream", ""),
    DOC("application/msword", ".doc"),
    PDF("application/pdf", ".pdf"),
    XLS("application/vnd.ms-excel", ".xls"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    PPT("application/vnd.ms-powerpoint", ".ppt");
    private String value;
    private String suffix;

    MimeType(String value, String suffix) {
        this.value = value;
        this.suffix = suffix;
    }

    public String getValue() {
        return value;
    }

    public String getSuffix() {
        return suffix;
    }

    public static String getSuffix(String value) {
        for (MimeType type : values()) {
            if (type.getValue().equals(value)) {
                return type.getSuffix();
            }
        }
        return "";
    }

    public static String getValue(String suffix) {
        for (MimeType type : values()) {
            if (type.getSuffix().equals(suffix)) {
                return type.getValue();
            }
        }
        return "";
    }
}
