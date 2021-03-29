/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.constvalue;

public enum FormAttributeAuthType {
    COMPONENT("component", "组件"),
    ROW("row", "行");
    private String value;
    private String text;

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    private FormAttributeAuthType(String value, String text) {
        this.value = value;
        this.text = text;
    }

}
