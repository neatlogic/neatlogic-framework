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
