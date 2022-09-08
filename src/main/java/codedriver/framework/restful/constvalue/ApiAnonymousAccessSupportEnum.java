/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.constvalue;

/**
 * 接口是否支持匿名访问
 */
public enum ApiAnonymousAccessSupportEnum {
    ANONYMOUS_ACCESS_FORBIDDEN(false, false), // 不支持匿名访问
    ANONYMOUS_ACCESS_WITH_ENCRYPTION(true, true), // 支持匿名访问但token需要加密
    ANONYMOUS_ACCESS_WITHOUT_ENCRYPTION(true, false); // 支持匿名访问且toen无需加密

    final private boolean anonymousAccessIsSupported; // 是否支持匿名访问
    final private boolean tokenEncryptionIsRequired; // token是否需要加密

    public boolean anonymousAccessIsSupported() {
        return anonymousAccessIsSupported;
    }

    public boolean tokenEncryptionIsRequired() {
        return tokenEncryptionIsRequired;
    }

    ApiAnonymousAccessSupportEnum(boolean anonymousAccessIsSupported, boolean tokenEncryptionIsRequired) {
        this.anonymousAccessIsSupported = anonymousAccessIsSupported;
        this.tokenEncryptionIsRequired = tokenEncryptionIsRequired;
    }
}
