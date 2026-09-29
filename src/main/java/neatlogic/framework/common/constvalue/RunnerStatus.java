package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.I18n;

import java.util.List;

public enum RunnerStatus implements IEnum {
    CONNECTED("connected", new I18n("common.connected")),
    DISCONNECTED("disconnected", new I18n("common.disconnected"));
    private final String value;
    private final I18n text;

    RunnerStatus(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text.toString();
    }

    public static String getText(String value) {
        for (RunnerStatus s : RunnerStatus.values()) {
            if (s.getValue().equals(value)) {
                return s.getText();
            }
        }
        return null;
    }

    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (RunnerStatus type : values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", type.getValue());
                    this.put("text", type.getText());
                }
            });
        }
        return array;
    }
}
