package codedriver.framework.exception.core;

public class ApiFieldValidNotFoundException extends ApiRuntimeException {
    public ApiFieldValidNotFoundException(String msg) {
        super("参数：" + msg + " 校验方法不存在");
    }
}
