package codedriver.framework.exception.runner;

import codedriver.framework.exception.core.ApiRuntimeException;

public class RunnerIsExistException extends ApiRuntimeException {
    public RunnerIsExistException(String host, Integer port) {
        super("ip和端口为：" + host + ":" + port + "的runner已存在");
    }
}
