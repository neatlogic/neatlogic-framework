/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.runner;

public class RunnerNotFoundInGroupException extends RuntimeException {
    public RunnerNotFoundInGroupException(Long runnerGroupId) {
        super("在id为：" + runnerGroupId + "的runner组里找不到runner");
    }
}
