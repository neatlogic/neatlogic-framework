/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.runner;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class RunnerIdNotFoundException extends ApiRuntimeException {
    public RunnerIdNotFoundException(Long id) {
        super("runner id：" + id + "不存在");
    }
}
