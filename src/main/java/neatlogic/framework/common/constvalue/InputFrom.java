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

package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public enum InputFrom implements IEnum {
    PAGE("page", new I18n("enum.framework.inputfrom.page")),
    IMPORT("import", new I18n("enum.framework.inputfrom.import")),
    RESTFUL("restful", new I18n("enum.framework.inputfrom.restful")),
    ITSM("itsm", new I18n("enum.framework.inputfrom.itsm")),
    UNKNOWN("unknown", new I18n("enum.framework.inputfrom.unknown")),
    CRON("cron", new I18n("common.scheduledtask")),
    AUTOEXEC("autoexec", new I18n("common.automaticcollection")),
    RELATIVE("relative", new I18n("enum.framework.inputfrom.relative"));

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
        return I18nUtils.getMessage(text.toString());
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
