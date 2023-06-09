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
