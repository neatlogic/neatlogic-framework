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
    private static final Log logger = LogFactory.getLog(CachedThreadPool.class);
    private static final Map<Long, ThreadVo> threadMap = new ConcurrentHashMap<>();
    private static final Set<String> threadSet = new HashSet<>();
    private static final PriorityBlockingQueue<NeatLogicThread> threadQueue = new PriorityBlockingQueue<>();

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
                threadVo.setPriority(nt.getPriority());
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
        List<ThreadVo> threads = new ArrayList<>(threadMap.values());
        threadPoolVo.setThreadList(threads);
        threadPoolVo.setMainPoolSize(mainThreadPool.getPoolSize());
        threadPoolVo.setMainActiveCount(mainThreadPool.getActiveCount());
        threadPoolVo.setMainQueueSize(threadQueue.size());
        return threadPoolVo;
    }
}
