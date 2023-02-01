/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.runner;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class RunnerNameRepeatsException extends ApiRuntimeException {
    public RunnerNameRepeatsException(String name) {
        super("已存在名称为：" + name + "的runner");
    }
}