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
import neatlogic.framework.dto.healthcheck.ThreadVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.*;

public class CachedThreadPool {
    static int cpu = Runtime.getRuntime().availableProcessors();
    //static int queueLen = 100000;
    private static final Log logger = LogFactory.getLog(CachedThreadPool.class);
    //private static List<ThreadVo> threadList = new ArrayList<>();
    private static final Map<Long, ThreadVo> threadMap = new ConcurrentHashMap<>();
    private static final Set<String> threadSet = new HashSet<>();
    // 创建非阻塞队列
    private static final ConcurrentLinkedQueue<Runnable> threadQueue = new ConcurrentLinkedQueue<>();
    /*
    主线程池，直接创建线程快速处理任务
     */
    private static final ThreadPoolExecutor mainThreadPool = new ThreadPoolExecutor(0, cpu * 15,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new NeatLogicRejectHandler()) {
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            if (r instanceof NeatLogicThread) {
                NeatLogicThread nt = (NeatLogicThread) r;
                ThreadVo threadVo = new ThreadVo();
                threadVo.setId(nt.getId());
                threadVo.setName(nt.getThreadName() + "#" + nt.getId());
                threadVo.setPoolName("main");
                threadVo.setStartTime(new Date());
                threadMap.put(nt.getId(), threadVo);
                threadSet.add(nt.getThreadName());
            }
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            // 任务完成后从 activeTasks 中移除
            if (r instanceof NeatLogicThread) {
                NeatLogicThread task = (NeatLogicThread) r;
                threadMap.remove(task.getId());
                threadSet.remove(task.getThreadName());
            }
            //尝试从队列中拿出任务处理
            Runnable task = threadQueue.poll();
            if (task != null) {
                mainThreadPool.execute(task);
            }
        }
    };
    /*
    备份线程池，当主线程池满了以后启用，队列满了以后开始抛异常并丢弃该任务
     */
    /*private static final ThreadPoolExecutor backupThreadPool = new ThreadPoolExecutor(0, cpu * 2,
            0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(queueLen), new ThreadPoolExecutor.AbortPolicy()) {
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            if (r instanceof NeatLogicThread) {
                NeatLogicThread nt = (NeatLogicThread) r;
                ThreadVo threadVo = new ThreadVo();
                threadVo.setId(nt.getId());
                threadVo.setName(nt.getThreadName() + "#" + nt.getId());
                threadVo.setPoolName("backup");
                threadVo.setStartTime(new Date());
                threadMap.put(nt.getId(), threadVo);
            }
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            // 任务完成后从 activeTasks 中移除
            if (r instanceof NeatLogicThread) {
                NeatLogicThread task = (NeatLogicThread) r;
                threadMap.remove(task.getId());
            }
        }
    };*/

    public static void execute(NeatLogicThread command, CountDownLatch countDownLatch) {
        command.setCountDownLatch(countDownLatch);
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
            /*if (r instanceof NeatLogicThread) {
                logger.warn("main thread pool(size:" + (cpu * 15) + ") is full, " + ((NeatLogicThread) r).getThreadName() + " is taking over by backup thread pool(size:" + (cpu * 2) + ").");
            } else {
                logger.warn("main thread pool(size:" + (cpu * 15) + ") is full, unknown thread is taking over by backup thread pool(size:" + (cpu * 2) + ").");
            }
            backupThreadPool.execute(r);
             */
            //进入等待队列
            threadQueue.offer(r);
        }
    }

    /*public static int getThreadActiveCount() {
        return mainThreadPool.getActiveCount();
    }*/

    public static ThreadPoolVo getStatus() {
        ThreadPoolVo threadPoolVo = new ThreadPoolVo();
        List<ThreadVo> threads = new ArrayList<>(threadMap.values());
        threadPoolVo.setThreadList(threads);
        threadPoolVo.setMainPoolSize(mainThreadPool.getPoolSize());
        threadPoolVo.setMainActiveCount(mainThreadPool.getActiveCount());
        threadPoolVo.setMainQueueSize(threadQueue.size());
        return threadPoolVo;
    }
}
