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

package neatlogic.framework.restful.enums;

import neatlogic.framework.util.$;

public enum ApiType {
    RAW("raw", "原始模式", "raw/"),
    OBJECT("object", "对象模式", "rest/"),
    STREAM("stream", "json流模式", "stream/"),
    BINARY("binary", "字节流模式", "binary/"),
    METRIC("metric", "指标模式", "metrics/"),
    FETCH("fetch", "客户端拉取模式", "fetch/");

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
