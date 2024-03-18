/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public enum InputFrom implements IEnum {
    PAGE("page", new I18n("页面操作")),
    IMPORT("import", new I18n("excel导入")),
    RESTFUL("restful", new I18n("接口调用")),
    ITSM("itsm", new I18n("流程修改")),
    UNKNOWN("unknown", new I18n("未知")),
    CRON("cron", new I18n("定时任务")),
    AUTOEXEC("autoexec", new I18n("自动采集")),
    RELATIVE("relative", new I18n("级联更新"));

    private final String value;
    private final I18n text;

    InputFrom(String _value, I18n _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
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
