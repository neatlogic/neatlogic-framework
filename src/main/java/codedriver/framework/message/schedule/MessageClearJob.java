package codedriver.framework.message.schedule;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.message.dao.mapper.MessageMapper;
import codedriver.framework.scheduler.core.JobBase;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.util.UuidUtil;
import org.quartz.CronExpression;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * @Title: MessageClearJob
 * @Package codedriver.framework.message.schedule
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/1/13 16:28
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@Service
@DisallowConcurrentExecution
public class MessageClearJob extends JobBase {

    @Autowired
    private MessageMapper messageMapper;

    private String cron = "0 0 4 * * ?";

    @Override
    public String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-MESSAGE-CLEAR";
    }

    @Override
    public Boolean checkCronIsExpired(JobObject jobObject) {
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
        JobObject.Builder jobObjectBuilder = new JobObject.Builder(UuidUtil.randomUuid(), this.getGroupName(), this.getClassName(), TenantContext.get().getTenantUuid());
        JobObject jobObject = jobObjectBuilder.build();
        this.reloadJob(jobObject);
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws JobExecutionException {
        /** 删除message_recipient表数据 **/
        Long messageId = getMaxnMessageId(Config.NEW_MESSAGE_EXPIRED_DAY());
        if(messageId != null){
            messageMapper.deleteMessageRecipientByLessThanOrEqualMessageId(messageId);
        }
        /** 删除message和message_user表数据 **/
        messageId = getMaxnMessageId(Config.HISTORY_MESSAGE_EXPIRED_DAY());
        if(messageId != null){
            messageMapper.deleteMessageUserByLessThanOrEqualMessageId(messageId);
            messageMapper.deleteMessageByLessThanOrEqualId(messageId);
        }
    }
    /**
     * @Description: 根据时效天数计算出时效时间点，再根据时效时间点查出需要删除数据中的最大messageId，如果messageId为null,则无需删除
     * @Author: linbq
     * @Date: 2021/1/20 11:32
     * @Params:[expiredDay]
     * @Returns:java.lang.Long
     **/
    private Long getMaxnMessageId(int expiredDay){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - expiredDay);
        Date earliestSendingTime = calendar.getTime();
        return messageMapper.getMessageMaxIdByLessThanInsertTime(earliestSendingTime);
    }
}
