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

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

public enum ApiType {
    OBJECT("object", new I18n("enum.framework.apitype.object"), "rest/"),
    STREAM("stream", new I18n("enum.framework.apitype.stream"), "stream/"),
    BINARY("binary", new I18n("enum.framework.apitype.binary"), "binary/");

    private final String name;
    private final I18n text;
    private final String urlPre;

    ApiType(String _name, I18n _text, String _urlPre) {
        this.name = _name;
        this.text = _text;
        this.urlPre = _urlPre;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
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
