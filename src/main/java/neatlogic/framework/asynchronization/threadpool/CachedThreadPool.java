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
import neatlogic.framework.dto.healthcheck.ThreadPoolVo;
import neatlogic.framework.dto.healthcheck.ThreadTaskVo;
import neatlogic.framework.dto.healthcheck.ThreadVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.*;

public class CachedThreadPool {
    static int rank = 15;
    static int cpu = Runtime.getRuntime().availableProcessors();
    private static final Log logger = LogFactory.getLog(CachedThreadPool.class);
    private static final Map<Long, ThreadTaskVo> threadTaskMap = new ConcurrentHashMap<>();
    private static final Map<Long, ThreadVo> threadMap = new ConcurrentHashMap<>();
    private static final Set<String> threadSet = ConcurrentHashMap.newKeySet();
    private static final PriorityBlockingQueue<NeatLogicThread> threadQueue = new PriorityBlockingQueue<>();

    static class NeatLogicThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r) {
                @Override
                public void run() {
                    try {
                        super.run();
                    } finally {
                        threadMap.remove(this.getId());
                    }
                }
            };
            threadMap.put(thread.getId(), new ThreadVo(thread.getId(), thread.getName()));
            return thread;
        }
    }

    private static final ThreadPoolExecutor mainThreadPool = new ThreadPoolExecutor(0, cpu * rank,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new NeatLogicThreadFactory(), new NeatLogicRejectHandler()) {
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            if (r instanceof NeatLogicThread) {
                NeatLogicThread nt = (NeatLogicThread) r;
                nt.setId(t.getId());
                ThreadTaskVo threadVo = new ThreadTaskVo();
                threadVo.setId(t.getId());
                threadVo.setName(nt.getThreadName());
                threadVo.setPoolName("main");
                threadVo.setStartTime(new Date());
                threadVo.setPriority(nt.getPriority());
                threadTaskMap.put(nt.getId(), threadVo);
                threadSet.add(nt.getThreadName());
            }
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);

            // 任务完成后从 activeTasks 中移除
            if (r instanceof NeatLogicThread) {
                NeatLogicThread task = (NeatLogicThread) r;
                threadTaskMap.remove(task.getId());
                threadSet.remove(task.getThreadName());
            }
            //尝试从队列中拿出任务处理
            Runnable task = threadQueue.poll();
            if (task != null) {
                mainThreadPool.execute(task);
            }
        }
    };

    public static void execute(NeatLogicThread command, CountDownLatch countDownLatch) {
        command.setCountDownLatch(countDownLatch);
        execute(command);
    }

    public static void execute(NeatLogicThread command, Semaphore lock) {
        command.setLock(lock);
        execute(command);
    }

    public static void execute(NeatLogicThread command) {
        try {
            boolean isExists = command.isUnique() && StringUtils.isNotBlank(command.getThreadName()) && threadSet.contains(command.getThreadName());
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
            //进入等待队列
            if (r instanceof NeatLogicThread) {
                threadQueue.offer((NeatLogicThread) r);
            } else {
                logger.error("线程池已满，非NeatLogicThread子类线程将被抛弃");
            }
        }
    }


    public static ThreadPoolVo getStatus() {
        ThreadPoolVo threadPoolVo = new ThreadPoolVo();
        List<ThreadTaskVo> threadTasks = new ArrayList<>(threadTaskMap.values());
        List<ThreadVo> threads = new ArrayList<>(threadMap.values());
        threadPoolVo.setThreadTaskList(threadTasks);
        threadPoolVo.setThreadList(threads);
        threadPoolVo.setMaxThreadCount(cpu * rank);
        threadPoolVo.setMainPoolSize(mainThreadPool.getPoolSize());
        threadPoolVo.setMainActiveCount(mainThreadPool.getActiveCount());
        threadPoolVo.setMainQueueSize(threadQueue.size());
        return threadPoolVo;
    }
}
