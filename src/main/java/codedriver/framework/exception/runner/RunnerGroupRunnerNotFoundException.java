/*
 * Copyright(c) 2021. TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.runner;

import codedriver.framework.exception.core.ApiRuntimeException;

public class RunnerGroupRunnerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -4324826681772207554L;

    public RunnerGroupRunnerNotFoundException(String name) {
        super("runnerGroup: " + name + "未配置runner");
    }


}
