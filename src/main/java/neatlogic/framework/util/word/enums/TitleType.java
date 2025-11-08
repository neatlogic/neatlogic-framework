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
package neatlogic.framework.util.word.enums;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

/**
 * @author longrf
 * @date 2022/9/26 14:23
 */

public enum TitleType {
    H1("h1", new I18n("标题 1"), true, "000000", new I18n("楷体"), 18, 1),
    H2("h2", new I18n("标题 2"), true, "000000", new I18n("楷体"), 15, 2),
    H3("h3", new I18n("标题 3"), true, "000000", new I18n("楷体"), 14, 3),
    TILE("centered_tile", new I18n("居中标题"), true, "000000", new I18n("楷体"), 15, 0),
    ;

    private final String value;
    private final I18n text;
    private final Boolean bold;
    private final String color;
    private final I18n fontFamily;
    private final Integer fontSize;
    private final Integer headingLevel;

    TitleType(String value, I18n text, Boolean bold, String color, I18n fontFamily, Integer fontSize, Integer headingLevel) {
        this.value = value;
        this.text = text;
        this.bold = bold;
        this.color = color;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.headingLevel = headingLevel;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }

    public Boolean getBold() {
        return bold;
    }

    public String getColor() {
        return color;
    }

    public String getFontFamily() {
        return $.t(fontFamily.toString());
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public Integer getHeadingLevel() {
        return headingLevel;
    }
}
