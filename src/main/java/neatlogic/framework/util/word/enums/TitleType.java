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
