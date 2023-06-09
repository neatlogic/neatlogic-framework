package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

import java.util.List;

public enum TeamLevel implements IEnum {

    GROUP("group", new I18n("集团"), 1),
    COMPANY("company", new I18n("公司"), 2),
    CENTER("center", new I18n("中心"), 3),
    DEPARTMENT("department", new I18n("部门"), 4),
    TEAM("team", new I18n("组"), 5);
    private String value;
    private I18n text;
    private int level;

    private TeamLevel(String value, I18n text, int level) {
        this.value = value;
        this.text = text;
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }

    public int getLevel() {
        return level;
    }

    public static String getValue(String _value) {
        for (TeamLevel type : values()) {
            if (type.getValue().equals(_value)) {
                return type.getValue();
            }
        }
        return null;
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (TeamLevel level : TeamLevel.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", level.getValue());
                    this.put("text", level.getText());
                }
            });
        }
        return array;
    }
}
