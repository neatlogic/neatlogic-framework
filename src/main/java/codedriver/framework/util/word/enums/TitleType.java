/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.word.enums;

/**
 * @author longrf
 * @date 2022/9/26 14:23
 */

public enum TitleType {
    H1("h1", "标题 1", true, "000000", "宋体", 20, 1),
    H2("h2", "标题 2", true, "000000", "宋体", 18, 2),
    H3("h3", "标题 3", true, "000000", "宋体", 16, 3),
    TILE("centered_tile", "居中标题", true, "000000", "宋体", 20, 0),
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
