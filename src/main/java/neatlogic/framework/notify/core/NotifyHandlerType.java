/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
