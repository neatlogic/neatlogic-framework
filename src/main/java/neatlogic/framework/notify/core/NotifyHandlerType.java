/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
