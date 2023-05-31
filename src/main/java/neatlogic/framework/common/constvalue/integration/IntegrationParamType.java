package neatlogic.framework.common.constvalue.integration;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

public enum IntegrationParamType {
    NUMBER("number", new I18n("common.number")),
    STRING("string", new I18n("common.char")),
    JSONOBJECT("jsonObject", new I18n("common.jsonobject")),
    JSONARRAY("jsonArray", new I18n("common.jsonarray"));

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
        return I18nUtils.getMessage(text.toString());
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
