/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package neatlogic.framework.util.word.enums;

/**
 * @author longrf
 * @date 2022/9/26 17:47
 */

public enum TableColor {
    BLACK("000000", "黑色"),
    WHITE("ffffff", "白色"),
    RED("FF0000", "红色"),
    BLUE("0000FF", "蓝色"),
    GREY("808080", "灰色"),
    ;

    private final String value;
    private final String text;

    private TableColor(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
