package codedriver.framework.matrix.dto;

import java.util.List;

public class MatrixFilterVo {
    private String uuid;
    private List<String> valueList;
    private String expression;

    public MatrixFilterVo(String uuid, String expression, List<String> valueList) {
        this.uuid = uuid;
        this.expression = expression;
        this.valueList = valueList;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
