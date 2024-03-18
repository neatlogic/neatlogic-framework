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

package neatlogic.module.framework.message.schedule.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.message.dao.mapper.MessageMapper;
import neatlogic.framework.scheduler.core.JobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import org.quartz.CronExpression;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
@DisallowConcurrentExecution
public class MessageClearJob extends JobBase {

    @Autowired
    private MessageMapper messageMapper;

    private final String cron = "0 0 4 * * ?";

    @Override
    public String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-MESSAGE-CLEAR";
    }

    @Override
    public Boolean isMyHealthy(JobObject jobObject) {
        return true;
    }

    @Override
    public void reloadJob(JobObject jobObject) {
        if (CronExpression.isValidExpression(cron)) {
            JobObject.Builder newJobObjectBuilder = new JobObject.Builder(jobObject.getJobName(), this.getGroupName(), this.getClassName(), TenantContext.get().getTenantUuid()).withCron(cron);
            JobObject newJobObject = newJobObjectBuilder.build();
            schedulerManager.loadJob(newJobObject);
        }
    }

    @Override
    public void initJob(String tenantUuid) {
        JobObject.Builder jobObjectBuilder = new JobObject.Builder(this.getGroupName(), this.getGroupName(), this.getClassName(), TenantContext.get().getTenantUuid());
        JobObject jobObject = jobObjectBuilder.build();
        this.reloadJob(jobObject);
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws JobExecutionException {
        /* 删除message_recipient表数据 **/
        Long messageId = getMaxMessageId(Config.NEW_MESSAGE_EXPIRED_DAY());
        if (messageId != null) {
            messageMapper.deleteMessageRecipientByLessThanOrEqualMessageId(messageId);
        }
        /* 删除message和message_user表数据 **/
        messageId = getMaxMessageId(Config.HISTORY_MESSAGE_EXPIRED_DAY());
        if (messageId != null) {
            messageMapper.deleteMessageUserByLessThanOrEqualMessageId(messageId);
            messageMapper.deleteMessageByLessThanOrEqualId(messageId);
        }
    }

    /**
     * @Description: 根据时效天数计算出时效时间点，再根据时效时间点查出需要删除数据中的最大messageId，如果messageId为null,则无需删除
     * @Author: linbq
     * @Date: 2021/1/20 11:32
     **/
    private Long getMaxMessageId(int expiredDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - expiredDay);
        Date earliestSendingTime = calendar.getTime();
        return messageMapper.getMessageMaxIdByLessThanInsertTime(earliestSendingTime);
    }
}
