package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.$;

import java.util.List;

public enum RunnerStatus implements IEnum {
    CONNECTED("connected", "已连接"),
    DISCONNECTED("disconnected", "未连接");
    private final String value;
    private final String text;

    RunnerStatus(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text);
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
