/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

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
