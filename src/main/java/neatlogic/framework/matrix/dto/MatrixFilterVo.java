package neatlogic.framework.matrix.dto;

import java.util.List;

public class MatrixFilterVo {
    private String uuid;
    private String name;
    private String type;
    private List<String> valueList;
    private String expression;

    public MatrixFilterVo() {
    }

    public MatrixFilterVo(String uuid, String expression, List<String> valueList) {
        this.uuid = uuid;
        this.expression = expression;
        this.valueList = valueList;
    }

    public MatrixFilterVo(String uuid, String type, String expression, List<String> valueList) {
        this.uuid = uuid;
        this.type = type;
        this.expression = expression;
        this.valueList = valueList;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
