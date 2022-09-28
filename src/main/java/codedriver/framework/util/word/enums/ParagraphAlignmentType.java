/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.word.enums;

/**
 * @author longrf
 * @date 2022/9/26 17:28
 */

public enum ParagraphAlignmentType {

    LEFT(1, "left"),
    CENTER(2, "center"),
    RIGHT(3, "right"),
    BOTH(4, "both"),
    ;

    private final Integer value;
    private final String text;

    private ParagraphAlignmentType(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
