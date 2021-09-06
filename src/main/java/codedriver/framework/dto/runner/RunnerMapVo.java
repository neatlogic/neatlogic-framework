/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.runner;

/**
 * @author lvzk
 * @since 2021/6/16 19:00
 **/
public class RunnerMapVo extends RunnerVo {
    private static final long serialVersionUID = 6043891449299751917L;
    private Integer runnerMapId;

    public Integer getRunnerMapId() {
        return runnerMapId;
    }

    public void setRunnerMapId(Integer runnerMapId) {
        this.runnerMapId = runnerMapId;
    }
}
