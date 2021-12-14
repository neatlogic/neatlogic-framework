/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public enum InputFrom implements IEnum {
    PAGE("page", "页面操作"), IMPORT("import", "excel导入"), RESTFUL("restful", "接口调用"),
    ITSM("itsm", "流程修改"), UNKNOWN("unknown", "未知"), CRON("cron", "定时任务"), AUTOEXEC("autoexec", "自动采集"), RELATIVE("relative", "级联更新");

    private final String value;
    private final String text;

    InputFrom(String _value, String _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static InputFrom get(String value) {
        if (StringUtils.isNotBlank(value)) {
            for (InputFrom s : InputFrom.values()) {
                if (s.getValue().equals(value)) {
                    return s;
                }
            }
        }
        return null;
    }

    public static String getValue(String _status) {
        for (InputFrom s : InputFrom.values()) {
            if (s.getValue().equals(_status)) {
                return s.getValue();
            }
        }
        return null;
    }

    public static String getText(String name) {
        for (InputFrom s : InputFrom.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }

    @Override
    public List getValueTextList() {
        JSONArray returnList = new JSONArray();
        for (InputFrom input : InputFrom.values()) {
            if (input != InputFrom.UNKNOWN) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("value", input.getValue());
                jsonObj.put("text", input.getText());
                returnList.add(jsonObj);
            }
        }
        return returnList;
    }
}
