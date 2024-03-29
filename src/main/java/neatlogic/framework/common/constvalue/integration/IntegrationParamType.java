package neatlogic.framework.common.constvalue.integration;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

public enum IntegrationParamType {
    NUMBER("number", new I18n("数字")),
    STRING("string", new I18n("字符型")),
    JSONOBJECT("jsonObject", new I18n("json对象")),
    JSONARRAY("jsonArray", new I18n("json数组"));

    private String name;
    private I18n text;

    private IntegrationParamType(String _name, I18n _text) {
        this.name = _name;
        this.text = _text;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return $.t(text.toString());
    }

    public static String getText(String name) {
        for (ApiParamType s : ApiParamType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }

}
