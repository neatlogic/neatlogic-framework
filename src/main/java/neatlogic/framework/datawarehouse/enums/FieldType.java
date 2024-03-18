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
import neatlogic.framework.util.I18n;

public enum FieldType {
    TEXT("text", new I18n("文本")),
    DATETIME("datetime", new I18n("日期时间")),
    DATE("date", new I18n("日期")),
    TIME("time", new I18n("时间")),
    NUMBER("number", new I18n("数字"));

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
        return $.t(text.toString());
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
