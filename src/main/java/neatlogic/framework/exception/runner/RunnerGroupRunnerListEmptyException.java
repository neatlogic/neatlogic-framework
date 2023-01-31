/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.runner;

import neatlogic.framework.dto.runner.RunnerGroupVo;
import neatlogic.framework.exception.core.ApiRuntimeException;

public class RunnerGroupRunnerListEmptyException extends ApiRuntimeException {

    public RunnerGroupRunnerListEmptyException(RunnerGroupVo groupVo) {
        super("runner group: "+groupVo.getName()+"("+groupVo.getId()+") 不存在runner，请检查runner管理");
    }
}
