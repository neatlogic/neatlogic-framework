package codedriver.framework.integration.authentication.costvalue;

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
