package codedriver.framework.exception.runner;

import codedriver.framework.exception.core.ApiRuntimeException;

public class RunnerIsUsedByRunnerGroupException extends ApiRuntimeException {
    public RunnerIsUsedByRunnerGroupException() {
        super("当前Runner已被Runner组关联，请解除所有关联后再删除Runner");
    }
}
