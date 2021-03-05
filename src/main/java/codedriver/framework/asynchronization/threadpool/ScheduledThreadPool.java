package codedriver.framework.asynchronization.threadpool;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Title: ScheduledThreadPool
 * @Package: codedriver.framework.asynchronization.threadpool
 * @Description: 定时执行线程池
 * @author: chenqiwei
 * @date: 2021/3/19:50 上午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class ScheduledThreadPool {
    private static final Log logger = LogFactory.getLog(CachedThreadPool.class);
    private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

    public static void execute(CodeDriverThread command, long delay) {
        try {
            scheduledThreadPool.schedule(command, delay, TimeUnit.SECONDS);
        } catch (RejectedExecutionException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

}
