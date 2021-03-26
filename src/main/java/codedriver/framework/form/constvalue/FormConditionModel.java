/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.constvalue;

public enum FormConditionModel {
    SIMPLE("simple", "简单模式"), CUSTOM("custom", "自定义模式");
    private String value;
    private String name;

    private FormConditionModel(String _value, String _name) {
        this.value = _value;
        this.name = _name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static FormConditionModel getFormConditionModel(String _value) {
        for (FormConditionModel s : FormConditionModel.values()) {
            if (s.getValue().equals(_value)) {
                return s;
            }
        }
        return null;
    }

    public static String getValue(String _value) {
        for (FormConditionModel s : FormConditionModel.values()) {
            if (s.getValue().equals(_value)) {
                return s.getValue();
            }
        }
        return null;
    }

    public static String getName(String _value) {
        for (FormConditionModel s : FormConditionModel.values()) {
            if (s.getValue().equals(_value)) {
                return s.getName();
            }
        }
        return "";
    }

}
