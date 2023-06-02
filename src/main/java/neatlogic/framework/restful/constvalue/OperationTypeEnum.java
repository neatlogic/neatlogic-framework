package neatlogic.framework.restful.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18nUtils;

import java.util.List;

public enum OperationTypeEnum implements IEnum {
    CREATE("create", $.st("增加")),
    DELETE("delete", $.st("删除")),
    UPDATE("update", $.st("更新")),
    SEARCH("search", $.st("查询")),
    OPERATE("operate", $.st("操作"));
    private String name;
    private String text;

    private OperationTypeEnum(String _value, String _text) {
        this.name = _value;
        this.text = _text;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return I18nUtils.getMessage(text);
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
