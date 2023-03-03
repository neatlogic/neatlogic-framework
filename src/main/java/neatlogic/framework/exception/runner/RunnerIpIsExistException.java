package neatlogic.framework.exception.runner;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class RunnerIpIsExistException extends ApiRuntimeException {
    public RunnerIpIsExistException(String host) {
        super("ip为：" + host +"的runner已存在");
    }
}
