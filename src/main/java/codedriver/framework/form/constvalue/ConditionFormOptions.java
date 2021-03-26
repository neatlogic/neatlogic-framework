/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.constvalue;

public enum ConditionFormOptions {
    FORMSELECT("formselect", "下拉框"),
    FORMINPUT("forminput", "文本框"),
    FORMTEXTAREA("formtextarea", "文本域"),
    FORMEDITOR("formeditor", "富文本框"),
    FORMRADIO("formradio", "单选框"),
    FORMCHECKBOX("formcheckbox", "复选框"),
    FORMDATE("formdate", "日期"),
    FORMTIME("formtime", "时间"),
    FORMCASCADELIST("formcascadelist", "级联下拉"),
    FORMUSERSELECT("formuserselect", "用户选择器");
    private String value;
    private String text;

    private ConditionFormOptions(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static ConditionFormOptions getConditionFormOption(String _value) {
        for (ConditionFormOptions e : values()) {
            if (e.value.equals(_value)) {
                return e;
            }
        }
        return null;
    }
}
