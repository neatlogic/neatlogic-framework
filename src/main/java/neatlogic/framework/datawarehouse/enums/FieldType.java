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

public enum FieldType {
    TEXT("text", new I18n("common.text")),
    DATETIME("datetime", new I18n("common.datatime")),
    DATE("date", new I18n("common.date")),
    TIME("time", new I18n("common.time")),
    NUMBER("number", new I18n("common.number"));

    private final String value;
    private final I18n text;

    FieldType(String _value, I18n _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }


    public static String getText(String name) {
        for (FieldType s : FieldType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }
}
