/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
    PASSWORD("password", "common.password"),
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
