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

package neatlogic.module.framework.scheduler;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.restful.core.SubmitKeyManager;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 清理重复提交map
 */
@Component
@DisallowConcurrentExecution
public class SubmitKeyClearJob extends ModuleInitializedListenerBase implements Job {
    private final Logger logger = LoggerFactory.getLogger(SubmitKeyClearJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        logger.debug("clear submit key map start!");
        SubmitKeyManager.clear();
        logger.debug("clear submit key map succeed!");
    }

    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        try {
            // 创建调度器
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // 定义JobDetail
            JobDetail jobDetail = JobBuilder.newJob(SubmitKeyClearJob.class)
                    .withIdentity("dailyJob", "SUBMIT_KEY_CLEAR")
                    .build();

            // 定义Trigger
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("dailyTrigger", "SUBMIT_KEY_CLEAR")
//                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
//                            .withIntervalInSeconds(1)
//                                    .repeatForever()
//                           // .withRepeatCount(0)
//                    )
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(3, 0)) // 每天3点执行
                    .build();

            // 将JobDetail和Trigger注册到调度器中
            scheduler.scheduleJob(jobDetail, trigger);

            // 启动调度器
            scheduler.start();
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected void myInit() {
        //ignore
    }
}
