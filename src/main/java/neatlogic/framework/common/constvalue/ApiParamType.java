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

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

public enum ApiParamType {
    INTEGER("int", new I18n("enum.framework.apiparamtype.integer")),
    ENUM("enum", new I18n("enum.framework.apiparamtype.enum")),
    BOOLEAN("boolean", new I18n("enum.framework.apiparamtype.boolean")),
    STRING("string", new I18n("enum.framework.apiparamtype.string")),
    LONG("long", new I18n("enum.framework.apiparamtype.long")),
    JSONOBJECT("jsonObject", new I18n("enum.framework.apiparamtype.jsonobject")),
    JSONARRAY("jsonArray", new I18n("enum.framework.apiparamtype.jsonarray")),
    IP("ip", new I18n("enum.framework.apiparamtype.ip")),
    EMAIL("email", new I18n("enum.framework.apiparamtype.email")),
    REGEX("regex", new I18n("enum.framework.apiparamtype.regex")),
    DOUBLE("double", new I18n("enum.framework.apiparamtype.double")),
    NOAUTH("noAuth", new I18n("enum.framework.apiparamtype.noauth")),
    FILE("file", new I18n("enum.framework.apiparamtype.file"));

    private String name;
    private I18n text;

    private ApiParamType(String _name, I18n _text) {
        this.name = _name;
        this.text = _text;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }

    public static String getText(String name) {
        for (ApiParamType s : ApiParamType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }

    public static ApiParamType getApiParamType(String name) {
        for (ApiParamType s : ApiParamType.values()) {
            if (s.getValue().equals(name)) {
                return s;
            }
        }
        return NOAUTH;
    }
}
