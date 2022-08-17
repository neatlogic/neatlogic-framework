/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.lcs.constvalue;

public enum ChangeType {
    INSERT("insert"),
    DELETE("delete"),
    UPDATE("update"),
    FILLBLANK("fillblank");
    private final String value;

    private ChangeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
