/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
