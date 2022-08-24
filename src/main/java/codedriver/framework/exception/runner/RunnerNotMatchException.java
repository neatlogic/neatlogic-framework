/*
 * Copyright(c) 2021. TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.runner;

import codedriver.framework.exception.core.ApiRuntimeException;

public class RunnerNotMatchException extends ApiRuntimeException {

    private static final long serialVersionUID = 3593220313941443951L;

    public RunnerNotMatchException(String ip) {
        super("ip: " + ip + "找不到匹配的runner，请核对runner组配置");
    }

    public RunnerNotMatchException() {
        super("找不到匹配的runner，请核对runner组配置 或者 没有配置runner map");
    }


}
