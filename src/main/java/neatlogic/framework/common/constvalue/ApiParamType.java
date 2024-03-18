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
