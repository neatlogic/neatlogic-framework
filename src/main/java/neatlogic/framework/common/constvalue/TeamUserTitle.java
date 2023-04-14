package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

import java.util.List;

public enum TeamUserTitle implements IEnum {

    DEPARTMENTLEADER("departmentleader", new I18n("enum.framework.teamusertitle.departmentleader")),
    TEAMLEADER("teamleader", new I18n("enum.framework.teamusertitle.teamleader")),
    CENTERLEADER("centerleader", new I18n("enum.framework.teamusertitle.centerleader")),
    COMPANYLEADER("companyleader", new I18n("enum.framework.teamusertitle.companyleader")),
    GROUPLEADER("groupleader", new I18n("enum.framework.teamusertitle.groupleader"));
    private String value;
    private I18n text;

    private TeamUserTitle(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }

    public static String getValue(String _value) {
        for (TeamUserTitle type : values()) {
            if (type.getValue().equals(_value)) {
                return type.getValue();
            }
        }
        return null;
    }

    public static String getText(String _value) {
        for (TeamUserTitle type : values()) {
            if (type.getValue().equals(_value)) {
                return type.getText();
            }
        }
        return "";
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (TeamUserTitle title : TeamUserTitle.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", title.getValue());
                    this.put("text", title.getText());
                }
            });
        }
        return array;
    }
}
