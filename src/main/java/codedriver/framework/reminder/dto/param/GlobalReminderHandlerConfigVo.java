package codedriver.framework.reminder.dto.param;

import com.alibaba.fastjson.JSONArray;

/**
 * @program: balantflow
 * @description: 控件参数类
 * @create: 2019-08-08 15:45
 **/
public class GlobalReminderHandlerConfigVo {
    private String showName;
    private String name;
    private String type;
    private JSONArray option;
    private String defaultValue;

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONArray getOption() {
        return option;
    }

    public void setOption(JSONArray option) {
        this.option = option;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
