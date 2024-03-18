/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
