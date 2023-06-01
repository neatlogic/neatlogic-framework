package neatlogic.framework.exception.core;

public class ApiFieldValidNotFoundException extends ApiRuntimeException {
    public ApiFieldValidNotFoundException(String msg) {
        super("参数：{0} 校验方法不存在", msg);
    }
}
