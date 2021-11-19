package codedriver.framework.exception.runner;

import codedriver.framework.exception.core.ApiRuntimeException;

public class RunnerIsUsedByJobException extends ApiRuntimeException {
    public RunnerIsUsedByJobException() {
        super(".当前Runner已被作业关联，无法删除");
    }
}
