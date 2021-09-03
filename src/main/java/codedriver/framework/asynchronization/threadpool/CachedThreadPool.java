/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.asynchronization.threadpool;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

public class CachedThreadPool {
    static int cpu = Runtime.getRuntime().availableProcessors();
    static int queueLen = 1000;
    private static final Log logger = LogFactory.getLog(CachedThreadPool.class);
    /*
    主线程池，直接创建线程快速处理任务
     */
    private static final ThreadPoolExecutor mainThreadPool = new ThreadPoolExecutor(0, cpu * 15,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new CodedriverRejectHandler());
    /*
    备份线程池，当主线程池满了以后启用，队列满了以后开始阻塞主线程
     */
    private static final ThreadPoolExecutor backupThreadPool = new ThreadPoolExecutor(0, cpu,
            0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(queueLen), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void execute(CodeDriverThread command) {
        try {
            mainThreadPool.execute(command);
        } catch (RejectedExecutionException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


    static class CodedriverRejectHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            logger.error("Main ThreadPool Is Full, Using Backup ThreadPool.");
            backupThreadPool.execute(r);
        }
    }

    public static int getThreadActiveCount() {
        return mainThreadPool.getActiveCount();
    }
}
