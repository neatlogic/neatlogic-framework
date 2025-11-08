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

package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public enum InputFrom implements IEnum {
    PAGE("page", "nfcc.inputfrom.page"),
    PC("pc", "nfcc.inputfrom.pc"),
    MOBILE("mobile", "nfcc.inputfrom.mobile"),
    IMPORT("import", "nfcc.inputfrom.excel"),
    RESTFUL("restful", "nfcc.inputfrom.api"),
    ITSM("itsm", "nfcc.inputfrom.itsm"),
    UNKNOWN("unknown", "nfcc.inputfrom.unknown"),
    CRON("cron", "common.schedule"),
    AUTOEXEC("autoexec", "nfcc.inputfrom.autocollect"),
    RELATIVE("relative", "nfcc.inputfrom.relative"),
    DIAGRAN("diagram", "term.diagram.name"),
    EVENT("event", "事件"),
    MQ("mq", "消息队列");

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
