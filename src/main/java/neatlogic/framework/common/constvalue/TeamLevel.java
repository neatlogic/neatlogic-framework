package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.$;

import java.util.List;

public enum TeamLevel implements IEnum {

    GROUP("group", 1),
    COMPANY("company", 2),
    CENTER("center", 3),
    DEPARTMENT("department", 4),
    TEAM("team", 5);
    private final String value;
    private final int level;

    TeamLevel(String value, int level) {
        this.value = value;
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据组织层级返回当前请求语言的显示文本。
     *
     * @return 当前请求语言下的组织层级名称
     */
    public String getText() {
        return switch (this) {
            case GROUP -> $.t("framework.teamlevel.group");
            case COMPANY -> $.t("framework.teamlevel.company");
            case CENTER -> $.t("framework.teamlevel.center");
            case DEPARTMENT -> $.t("framework.teamlevel.department");
            case TEAM -> $.t("framework.teamlevel.team");
        };
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
