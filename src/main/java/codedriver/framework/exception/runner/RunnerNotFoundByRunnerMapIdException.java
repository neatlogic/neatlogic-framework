/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.runner;

public class RunnerNotFoundByRunnerMapIdException extends RuntimeException {
    private static final long serialVersionUID = -3849705005655724969L;

    public RunnerNotFoundByRunnerMapIdException(Long runnerMapId) {
        super("未找到" + runnerMapId + "对应的runner");
    }
}
