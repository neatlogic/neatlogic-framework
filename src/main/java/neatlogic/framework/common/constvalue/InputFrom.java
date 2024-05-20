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
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public enum InputFrom implements IEnum {
    PAGE("page", "nfcc.inputfrom.page"),
    IMPORT("import", "nfcc.inputfrom.excel"),
    RESTFUL("restful", "nfcc.inputfrom.api"),
    ITSM("itsm", "nfcc.inputfrom.itsm"),
    UNKNOWN("unknown", "nfcc.inputfrom.unknown"),
    CRON("cron", "common.schedule"),
    AUTOEXEC("autoexec", "nfcc.inputfrom.autocollect"),
    RELATIVE("relative", "nfcc.inputfrom.relative"),
    DIAGRAN("diagram", "term.diagram.name");

    private final String value;
    private final String text;

    InputFrom(String _value, String _text) {
        this.value = _value;
        this.text = _text;
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text);
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
