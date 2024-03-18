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

package neatlogic.framework.asynchronization.threadpool;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;
import java.util.concurrent.*;

public class CachedThreadPool {
    static int cpu = Runtime.getRuntime().availableProcessors();
    static int queueLen = 100000;
    private static final Log logger = LogFactory.getLog(CachedThreadPool.class);
    /*
    主线程池，直接创建线程快速处理任务
     */
    private static final ThreadPoolExecutor mainThreadPool = new ThreadPoolExecutor(0, cpu * 15,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new NeatLogicRejectHandler());
    /*
    备份线程池，当主线程池满了以后启用，队列满了以后开始抛异常并丢弃该任务
     */
    private static final ThreadPoolExecutor backupThreadPool = new ThreadPoolExecutor(0, cpu * 2,
            0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(queueLen), new ThreadPoolExecutor.AbortPolicy());

    public static void execute(NeatLogicThread command) {
        try {
            boolean isExists = false;
            if (command.isUnique() && StringUtils.isNotBlank(command.getThreadName())) {
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                for (Thread t : threadSet) {
                    if (t.getName().equalsIgnoreCase(command.getThreadName())) {
                        isExists = true;
                        break;
                    }
                }
            }
            if (!isExists) {
                mainThreadPool.execute(command);
            } else {
                logger.warn(command.getThreadName() + " is running");
            }
        } catch (RejectedExecutionException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


    static class NeatLogicRejectHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof NeatLogicThread) {
                logger.warn("main thread pool(size:" + (cpu * 15) + ") is full, " + ((NeatLogicThread) r).getThreadName() + " is taking over by backup thread pool(size:" + (cpu * 2) + ").");
            } else {
                logger.warn("main thread pool(size:" + (cpu * 15) + ") is full, unknown thread is taking over by backup thread pool(size:" + (cpu * 2) + ").");
            }
            backupThreadPool.execute(r);
        }
    }

    public static int getThreadActiveCount() {
        return mainThreadPool.getActiveCount();
    }
}
