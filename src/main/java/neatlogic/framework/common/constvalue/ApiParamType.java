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

import neatlogic.framework.util.$;

public enum ApiParamType  {
    INTEGER("int", "common.int"),
    ENUM("enum", "common.enum"),
    BOOLEAN("boolean", "common.boolean"),
    STRING("string", "common.string"),
    LONG("long", "common.long"),
    JSONOBJECT("jsonObject", "common.jsonobject"),
    JSONARRAY("jsonArray", "common.jsonarray"),
    IP("ip", "ip"),
    EMAIL("email", "common.email"),
    REGEX("regex", "common.regex"),
    DOUBLE("double", "common.double"),
    NOAUTH("noAuth", "common.object"),
    FILE("file", "common.attachment");

    private final String name;
    private final String text;

    private ApiParamType(String _name, String _text) {
        this.name = _name;
        this.text = _text;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return $.t(text);
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
