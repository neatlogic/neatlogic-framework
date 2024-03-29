package neatlogic.framework.restful.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

import java.util.List;

/**
 * @author longrf
 * @date 2022/3/31 5:12 下午
 */
public enum IntegrationAuditStatus implements IEnum {
    SUCCEED("succeed", new I18n("成功")),
    FAILED("failed", new I18n("失败"));
    private final String value;
    private final I18n text;

    IntegrationAuditStatus(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
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
