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
import neatlogic.framework.util.I18n;

public enum ExpireUnit {
    MINUTE("minute", new I18n("分钟")),
    HOUR("hour", new I18n("小时")),
    DAY("day", new I18n("天"));

    private final String value;
    private final I18n text;

    ExpireUnit(String _value, I18n _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }


    public static String getText(String name) {
        for (ExpireUnit s : ExpireUnit.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }
}
