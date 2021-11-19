package codedriver.framework.exception.runner;

import codedriver.framework.exception.core.ApiRuntimeException;

public class RunnerIsExistException extends ApiRuntimeException {
    public RunnerIsExistException(String host) {
        super("ip为：" + host +"的runner已存在");
    }
}
