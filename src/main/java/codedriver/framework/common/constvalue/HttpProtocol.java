package codedriver.framework.common.constvalue;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public enum HttpProtocol implements IEnum {
    HTTP("http", "http"),
    HTTPS("https", "https");
    private final String value;
    private final String text;

    HttpProtocol(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (HttpProtocol type : values()) {
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

