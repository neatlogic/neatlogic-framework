package neatlogic.framework.matrix.dto;

public class MatrixKeywordFilterVo {
    private String uuid;
    private String value;
    private String expression;

    public MatrixKeywordFilterVo(String uuid, String expression, String value) {
        this.uuid = uuid;
        this.expression = expression;
        this.value = value;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
