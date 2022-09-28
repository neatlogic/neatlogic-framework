/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.word.enums;

/**
 * @author longrf
 * @date 2022/9/26 17:56
 */

public enum FontFamily {
    SONG("宋体"),
    BLACK("黑体"),
    FANG_SONG("仿宋"),
    REGULAR_SCRIPT("楷体"),
    ;

    private final String value;

    private FontFamily(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
