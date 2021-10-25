/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.authentication.enums;

import java.util.Objects;

public enum HttpMethod {
    GET("get"), POST("post");

    private final String type;

    HttpMethod(String _type) {
        this.type = _type;
    }

    public static HttpMethod getHttpMethod(String _value) {
        for (HttpMethod method : HttpMethod.values()) {
            if (Objects.equals(method.type, _value)) {
                return method;
            }
        }
        return null;
    }

    public String toString() {
        return this.type;
    }
}
