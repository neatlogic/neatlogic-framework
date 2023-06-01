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

package neatlogic.framework.datawarehouse.enums;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

public enum FieldInputType {
    TEXT("text", new I18n("文本框")),
    DATETIME("datetime", new I18n("日期时间")),
    SELECT("select", new I18n("下拉框")),
    RADIO("radio", new I18n("单选框")),
    CHECKBOX("checkbox", new I18n("复选框"));

    private final String value;
    private final I18n text;

    FieldInputType(String _value, I18n _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }

    public static String getValue(String _status) {
        for (FieldInputType s : FieldInputType.values()) {
            if (s.getValue().equals(_status)) {
                return s.getValue();
            }
        }
        return null;
    }

    public static String getText(String name) {
        for (FieldInputType s : FieldInputType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }
}
