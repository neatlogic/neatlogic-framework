package neatlogic.framework.asynchronization.threadpool;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Title: ScheduledThreadPool
 * @Package: neatlogic.framework.asynchronization.threadpool
 * @Description: 定时执行线程池
 * @author: chenqiwei
 * @date: 2021/3/19:50 上午
 **/
public class ScheduledThreadPool {
    private static final Log logger = LogFactory.getLog(CachedThreadPool.class);
    private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

    public static void execute(NeatLogicThread command, long delay) {
        try {
            scheduledThreadPool.schedule(command, delay, TimeUnit.SECONDS);
        } catch (RejectedExecutionException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

}
