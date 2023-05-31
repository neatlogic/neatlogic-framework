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

import neatlogic.framework.common.config.Config;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

import java.util.List;
import java.util.Objects;

public enum DeviceType implements IEnum {
    ALL("all", new I18n("common.all")),
    MOBILE("mobile", new I18n("enum.framework.devicetype.mobile")),
    PC("pc", new I18n("enum.framework.devicetype.pc"));

    private final String status;
    private final I18n text;

    DeviceType(String _status, I18n _text) {
        this.status = _status;
        this.text = _text;
    }

    public String getValue() {
        return status;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }

    public static String getValue(String _status) {
        for (DeviceType s : DeviceType.values()) {
            if (s.getValue().equals(_status)) {
                return s.getValue();
            }
        }
        return null;
    }

    public static String getText(String _status) {
        for (DeviceType s : DeviceType.values()) {
            if (s.getValue().equals(_status)) {
                return s.getText();
            }
        }
        return "";
    }

    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (DeviceType type : values()) {
            if (!Config.MOBILE_IS_ONLINE() && Objects.equals(type.getValue(), DeviceType.MOBILE.getValue())) {
                continue;
            }
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
