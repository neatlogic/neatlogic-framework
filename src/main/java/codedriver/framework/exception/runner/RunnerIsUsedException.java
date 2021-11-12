package codedriver.framework.exception.runner;

import codedriver.framework.exception.core.ApiRuntimeException;

public class RunnerIsUsedException extends ApiRuntimeException {
    public RunnerIsUsedException() {
        super("此runner已被引用，无法删除");
    }
}
