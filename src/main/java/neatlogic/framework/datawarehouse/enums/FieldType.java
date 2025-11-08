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

package neatlogic.framework.datawarehouse.enums;

import neatlogic.framework.util.$;

public enum FieldType {
    TEXT("text", "common.text"),
    DATETIME("datetime", "common.datetime"),
    DATE("date", "common.date"),
    TIME("time", "common.time"),
    NUMBER("number", "common.number");

    private final String value;
    private final String text;

    FieldType(String _value, String _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text);
    }

    public static String getValue(String name) {
        for (FieldType s : FieldType.values()) {
            if (s.getValue().equalsIgnoreCase(name)) {
                return s.getValue();
            }
        }
        return "";
    }

    public static String getText(String name) {
        for (FieldType s : FieldType.values()) {
            if (s.getValue().equalsIgnoreCase(name)) {
                return s.getText();
            }
        }
        return "";
    }
}
