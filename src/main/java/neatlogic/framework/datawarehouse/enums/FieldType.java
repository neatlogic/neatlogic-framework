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

package neatlogic.framework.datawarehouse.enums;

import neatlogic.framework.util.$;

public enum FieldType {
    TEXT("text", "common.text"),
    DATETIME("datetime", "common.datetime"),
    DATE("date", "common.date"),
    TIME("time", "common.time"),
    NUMBER("number", "common.number");

    private final String value;
    private final String text;

    FieldType(String _value, String _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text);
    }

    public static String getValue(String name) {
        for (FieldType s : FieldType.values()) {
            if (s.getValue().equalsIgnoreCase(name)) {
                return s.getValue();
            }
        }
        return "";
    }

    public static String getText(String name) {
        for (FieldType s : FieldType.values()) {
            if (s.getValue().equalsIgnoreCase(name)) {
                return s.getText();
            }
        }
        return "";
    }
}
