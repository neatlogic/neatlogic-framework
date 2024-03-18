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

package neatlogic.module.framework.notify.schedule.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.notify.core.INotifyContentHandler;
import neatlogic.framework.notify.core.INotifyHandler;
import neatlogic.framework.notify.core.NotifyContentHandlerFactory;
import neatlogic.framework.notify.core.NotifyHandlerFactory;
import neatlogic.framework.notify.dao.mapper.NotifyJobMapper;
import neatlogic.framework.notify.dto.NotifyVo;
import neatlogic.framework.notify.dto.job.NotifyJobVo;
import neatlogic.framework.notify.exception.NotifyContentHandlerNotFoundException;
import neatlogic.framework.notify.exception.NotifyHandlerNotFoundException;
import neatlogic.framework.scheduler.core.JobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@DisallowConcurrentExecution
public class NotifyContentJob extends JobBase {
	static Logger logger = LoggerFactory.getLogger(NotifyContentJob.class);

	@Autowired
    private NotifyJobMapper notifyJobMapper;

	@Override
    public Boolean isMyHealthy(JobObject jobObject) {
        NotifyJobVo jobVo = notifyJobMapper.getJobBaseInfoById(Long.valueOf(jobObject.getJobName()));
        if (jobVo != null) {
            if (jobVo.getIsActive().equals(1) && jobVo.getCron().equals(jobObject.getCron())) {
                return true;
            }
        }
        return false;
    }

	@Override
	public void reloadJob(JobObject jobObject) {
		String tenantUuid = jobObject.getTenantUuid();
		TenantContext.get().switchTenant(tenantUuid);
        NotifyJobVo jobVo = notifyJobMapper.getJobBaseInfoById(Long.valueOf(jobObject.getJobName()));
		if (jobVo != null && Objects.equals(jobVo.getIsActive(),1)) {
			JobObject newJobObject = new JobObject.Builder(jobVo.getId().toString(), this.getGroupName(), this.getClassName(), tenantUuid).withCron(jobVo.getCron()).addData("notifyContentJobId", jobVo.getId()).build();
			schedulerManager.loadJob(newJobObject);
		} else {
			schedulerManager.unloadJob(jobObject);
		}
	}

	@Override
	public void initJob(String tenantUuid) {
		/** 初始化所有可用的定时任务 */
		List<NotifyJobVo> jobList = notifyJobMapper.getAllActiveJob();
		if(CollectionUtils.isNotEmpty(jobList)){
			for(NotifyJobVo vo : jobList){
				JobObject newJobObject = new JobObject.Builder(vo.getId().toString(), this.getGroupName(), this.getClassName(), tenantUuid).withCron(vo.getCron()).addData("notifyContentJobId",vo.getId()).build();
				schedulerManager.loadJob(newJobObject);
			}
		}
	}

	@Override
	public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
		Long id = (Long) jobObject.getData("notifyContentJobId");
        NotifyJobVo job = notifyJobMapper.getJobBaseInfoById(id);
        INotifyContentHandler handler = NotifyContentHandlerFactory.getHandler(job.getHandler());
        INotifyHandler notifyHandler = NotifyHandlerFactory.getHandler(job.getNotifyHandler());
        if(handler == null){
            throw new NotifyContentHandlerNotFoundException(job.getHandler());
        }
        if(notifyHandler == null){
            throw new NotifyHandlerNotFoundException(job.getNotifyHandler());
        }

        List<NotifyVo> notifyData = handler.getNotifyData(id);
        if(CollectionUtils.isNotEmpty(notifyData)){
            for(NotifyVo vo : notifyData){
                notifyHandler.execute(vo);
            }
        }
	}


    @Override
	public String getGroupName() {
		return TenantContext.get().getTenantUuid() + "-NOTIFY-CONTENT";
	}

}
