/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.scheduler.core.SchedulerManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JobVo extends BasePageVo {

    @EntityField(name = "定时作业名称",
            type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "状态(0:禁用，1：启用)",
            type = ApiParamType.INTEGER)
    private Integer isActive;

    @EntityField(name = "定时作业uuid",
            type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "定时作业组件类路径",
            type = ApiParamType.STRING)
    private String handler;
    @EntityField(name = "定时作业组件名称",
            type = ApiParamType.STRING)
    private String handlerName;
    @EntityField(name = "是否保存执行记录(0:不保存，1:保存)",
            type = ApiParamType.INTEGER)
    private Integer needAudit;
    @EntityField(name = "cron表达式",
            type = ApiParamType.STRING)
    private String cron;
    @EntityField(name = "开始时间",
            type = ApiParamType.LONG)
    private Date beginTime;
    @EntityField(name = "结束时间",
            type = ApiParamType.LONG)
    private Date endTime;

    private JobStatusVo jobStatus;

    //	@EntityField(name = "定时作业属性列表",
//			type = ApiParamType.JSONARRAY)
    private List<JobPropVo> propList;

    public JobVo() {
        this.setPageSize(20);
    }

    public List<JobPropVo> getPropList() {
        if (propList == null || propList.size() == 0) {
            return null;
        }
        return propList;
    }

    public void setPropList(List<JobPropVo> propList) {
        this.propList = propList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }


    public String getUuid() {
        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString().replace("-", "");
        }
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getNeedAudit() {
        return needAudit;
    }

    public void setNeedAudit(Integer needAudit) {
        this.needAudit = needAudit;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getHandlerName() {
        if (handlerName != null) {
            return handlerName;
        }
        JobClassVo jobClassVo = SchedulerManager.getJobClassByClassName(handler);
        if (jobClassVo == null) {
            return null;
        }
        handlerName = jobClassVo.getName();
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public JobStatusVo getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatusVo jobStatus) {
        this.jobStatus = jobStatus;
    }

    //该租户是否拥有该作业的模块
    public Integer getIsTenantHasModuleGroup() {
        if (StringUtils.isNotBlank(this.handler)) {
            JobClassVo jobClass = SchedulerManager.getJobClassByClassName(this.handler);
            if (jobClass != null) {
                return TenantContext.get().containsModule(jobClass.getModuleId()) ? 1 : 0;
            }
        }
        return 0;
    }

}
