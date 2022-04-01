package codedriver.framework.restful.enums;

import codedriver.framework.common.constvalue.IEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author longrf
 * @date 2022/3/31 5:12 下午
 */
public enum IntegrationAuditStatus implements IEnum {
    SUCCEED("succeed", "成功"), FAILED("failed", "失败");
    private final String value;
    private final String text;

    IntegrationAuditStatus(String value, String text) {
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
        for (IntegrationAuditStatus type : values()) {
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
