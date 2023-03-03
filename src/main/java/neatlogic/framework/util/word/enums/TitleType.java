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

/**
 * @author longrf
 * @date 2022/9/26 14:23
 */

public enum TitleType {
    H1("h1", "标题 1", true, "000000", "楷体", 18, 1),
    H2("h2", "标题 2", true, "000000", "楷体", 15, 2),
    H3("h3", "标题 3", true, "000000", "楷体", 14, 3),
    TILE("centered_tile", "居中标题", true, "000000", "楷体", 15, 0),
    ;

    private final String value;
    private final String text;
    private final Boolean bold;
    private final String color;
    private final String fontFamily;
    private final Integer fontSize;
    private final Integer headingLevel;

    TitleType(String value, String text, Boolean bold, String color, String fontFamily, Integer fontSize, Integer headingLevel) {
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
        return text;
    }

    public Boolean getBold() {
        return bold;
    }

    public String getColor() {
        return color;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public Integer getHeadingLevel() {
        return headingLevel;
    }
}
