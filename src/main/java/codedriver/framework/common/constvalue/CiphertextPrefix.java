/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

/**
 * 密文前缀
 */
package codedriver.framework.common.constvalue;

public enum CiphertextPrefix {
    RC4("RC4:");

    private final String value;

    CiphertextPrefix(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
