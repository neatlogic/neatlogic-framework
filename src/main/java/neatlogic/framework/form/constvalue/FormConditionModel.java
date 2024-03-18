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

package neatlogic.framework.form.constvalue;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

public enum FormConditionModel {
    SIMPLE("simple", new I18n("简单模式")),
    CUSTOM("custom", new I18n("自定义模式"));
    private final String value;
    private final I18n name;

    FormConditionModel(String _value, I18n _name) {
        this.value = _value;
        this.name = _name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return $.t(name.toString());
    }

    public static FormConditionModel getFormConditionModel(String _value) {
        for (FormConditionModel s : FormConditionModel.values()) {
            if (s.getValue().equals(_value)) {
                return s;
            }
        }
        return null;
    }

    public static String getValue(String _value) {
        for (FormConditionModel s : FormConditionModel.values()) {
            if (s.getValue().equals(_value)) {
                return s.getValue();
            }
        }
        return null;
    }

    public static String getName(String _value) {
        for (FormConditionModel s : FormConditionModel.values()) {
            if (s.getValue().equals(_value)) {
                return s.getName();
            }
        }
        return "";
    }

}
