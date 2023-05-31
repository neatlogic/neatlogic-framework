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
package neatlogic.framework.util.word.enums;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

/**
 * @author longrf
 * @date 2022/9/26 14:23
 */

public enum TitleType {
    H1("h1", new I18n("enum.framework.titletype.h1"), true, "000000", new I18n("enum.framework.titletype.tilefont"), 18, 1),
    H2("h2", new I18n("enum.framework.titletype.h2"), true, "000000", new I18n("enum.framework.titletype.tilefont"), 15, 2),
    H3("h3", new I18n("enum.framework.titletype.h3"), true, "000000", new I18n("enum.framework.titletype.tilefont"), 14, 3),
    TILE("centered_tile", new I18n("enum.framework.titletype.tile.a"), true, "000000", new I18n("enum.framework.titletype.tilefont"), 15, 0),
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
        return I18nUtils.getMessage(text.toString());
    }

    public Boolean getBold() {
        return bold;
    }

    public String getColor() {
        return color;
    }

    public String getFontFamily() {
        return I18nUtils.getMessage(fontFamily.toString());
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public Integer getHeadingLevel() {
        return headingLevel;
    }
}
