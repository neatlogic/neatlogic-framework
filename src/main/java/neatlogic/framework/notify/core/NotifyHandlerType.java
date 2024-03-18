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

package neatlogic.framework.notify.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.util.$;

import java.util.List;

public enum NotifyHandlerType implements IEnum {

    EMAIL("email", "common.emailnotify"),
    MESSAGE("message", "common.messagenotify"),
    WECHAT("wechat", "common.wechatnotify");

    private final String value;
    private final String text;

    NotifyHandlerType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (NotifyHandlerType type : NotifyHandlerType.values()) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("value", type.getValue());
            jsonObj.put("text", type.getText());
            array.add(jsonObj);
        }
        return array;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text);
    }

    public static String getText(String _value) {
        for (NotifyHandlerType n : NotifyHandlerType.values()) {
            if (n.getValue().equals(_value)) {
                return n.getText();
            }
        }
        return "";
    }
}
