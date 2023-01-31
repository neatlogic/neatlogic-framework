/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.dto.runner;

/**
 * @author lvzk
 * @since 2021/6/16 19:00
 **/
public class RunnerMapVo extends RunnerVo {
    private static final long serialVersionUID = 6043891449299751917L;
    private Long runnerMapId;

    public RunnerMapVo() {

    }

    public RunnerMapVo(Long runnerMapId, Long runnerId) {
        this.runnerMapId = runnerMapId;
        super.setId(runnerId);
    }

    public RunnerMapVo(String runnerUrl, Long runnerMapId) {
        this.runnerMapId = runnerMapId;
        super.setUrl(runnerUrl);
    }

    public Long getRunnerMapId() {
        return runnerMapId;
    }

    public void setRunnerMapId(Long runnerMapId) {
        this.runnerMapId = runnerMapId;
    }
}
