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

package neatlogic.framework.scheduler.dto;

import java.util.Date;

@Deprecated
public class JobLoadTimeVo {
    private String jobName;
    private String jobGroup;
    private String cron;
    private Date loadTime;

    public JobLoadTimeVo() {

    }

    public JobLoadTimeVo(String jobName, String jobGroup) {
        this(jobName, jobGroup, null, null);
    }

    public JobLoadTimeVo(String jobName, String jobGroup, String cron, Date loadTime) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.cron = cron;
        this.loadTime = loadTime;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Date getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(Date loadTime) {
        this.loadTime = loadTime;
    }
}
