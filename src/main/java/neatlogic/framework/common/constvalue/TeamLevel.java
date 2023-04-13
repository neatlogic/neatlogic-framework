package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

import java.util.List;

public enum TeamLevel implements IEnum {

    GROUP("group", new I18n("enum.framework.teamlevel.group"), 1),
    COMPANY("company", new I18n("enum.framework.teamlevel.company"), 2),
    CENTER("center", new I18n("enum.framework.teamlevel.center"), 3),
    DEPARTMENT("department", new I18n("enum.framework.teamlevel.department"), 4),
    TEAM("team", new I18n("enum.framework.teamlevel.team"), 5);
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
        return I18nUtils.getMessage(text.toString());
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
