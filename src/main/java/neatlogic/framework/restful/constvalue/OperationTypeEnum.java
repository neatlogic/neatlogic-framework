package neatlogic.framework.restful.constvalue;

import neatlogic.framework.common.constvalue.IEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

import java.util.List;

public enum OperationTypeEnum implements IEnum {
    CREATE("create", new I18n("enum.framework.operationtypeenum.create")),
    DELETE("delete", new I18n("enum.framework.operationtypeenum.delete")),
    UPDATE("update", new I18n("enum.framework.operationtypeenum.update")),
    SEARCH("search", new I18n("enum.framework.operationtypeenum.search")),
    OPERATE("operate", new I18n("enum.framework.operationtypeenum.operate"));
    private String name;
    private I18n text;

    private OperationTypeEnum(String _value, I18n _text) {
        this.name = _value;
        this.text = _text;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }

    public static String getText(String value) {
        for (OperationTypeEnum f : OperationTypeEnum.values()) {
            if (f.getValue().equals(value)) {
                return f.getText();
            }
        }
        return "";
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (OperationTypeEnum typeEnum : OperationTypeEnum.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", typeEnum.getValue());
                    this.put("text", typeEnum.getText());
                }
            });
        }
        return array;
    }
}
