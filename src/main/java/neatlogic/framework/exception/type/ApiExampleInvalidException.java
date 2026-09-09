package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

/** 接口声明了无法解析的请求示例，不能向调用方发布不完整的调用说明。 */
public class ApiExampleInvalidException extends ApiRuntimeException {
    /** 保留底层解析原因，并标明错误声明所在的接口和场景。 */
    public ApiExampleInvalidException(String api, String title, Throwable cause) {
        super("nf.api.example.invalid", cause, api, title);
    }
}
