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

package neatlogic.framework.restful.enums;

import neatlogic.framework.util.$;

public enum ApiType {
    RAW("raw", "原始模式", "raw/"),
    OBJECT("object", "对象模式", "rest/"),
    STREAM("stream", "json流模式", "stream/"),
    BINARY("binary", "字节流模式", "binary/");

    private final String name;
    private final String text;
    private final String urlPre;

    ApiType(String _name, String _text, String _urlPre) {
        this.name = _name;
        this.text = _text;
        this.urlPre = _urlPre;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return $.t(text);
    }

    public String getUrlPre() {
        return urlPre;
    }

    public static String getText(String name) {
        for (ApiType s : ApiType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }

    public static String getUrlPre(String name) {
        for (ApiType s : ApiType.values()) {
            if (s.getValue().equals(name)) {
                return s.getUrlPre();
            }
        }
        return "";
    }
}
