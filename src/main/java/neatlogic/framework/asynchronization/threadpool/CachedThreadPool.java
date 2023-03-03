/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
