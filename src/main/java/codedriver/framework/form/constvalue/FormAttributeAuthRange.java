/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.constvalue;

public enum FormAttributeAuthRange {
    ALL("all", "所有");
    private String value;
    private String text;

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    private FormAttributeAuthRange(String value, String text) {
        this.value = value;
        this.text = text;
    }

}
