/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.form.constvalue;

public enum FormAttributeAction {
    HIDE("hide", "隐藏"),
    READ("read", "只读"),
    EDIT("edit", "编辑");
    private String value;
    private String text;

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    private FormAttributeAction(String value, String text) {
        this.value = value;
        this.text = text;
    }

}
