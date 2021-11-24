package codedriver.framework.exception.runner;

import codedriver.framework.exception.core.ApiRuntimeException;

public class RunnerIpIsExistException extends ApiRuntimeException {
    public RunnerIpIsExistException(String host) {
        super("ip为：" + host +"的runner已存在");
    }
}
