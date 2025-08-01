package neatlogic.framework.asynchronization.taskmanager;

import neatlogic.framework.asynchronization.queue.NeatLogicNonBlockingQueue;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.exception.core.ApiRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/*
动态创建线程处理异步任务
 */
public class AsyncTaskManager<T> {

    private static final Map<String, AsyncTaskManager<?>> INSTANCES = new ConcurrentHashMap<>();
    private final Logger logger;
    //非阻塞队列
    private final NeatLogicNonBlockingQueue<T> queue = new NeatLogicNonBlockingQueue<>();
    //记录线程数
    private final AtomicInteger activeWorkerCount = new AtomicInteger(0);
    //最多创建多少线程
    private final int maxWorkers;
    //线程名前缀
    private final String workerNamePrefix;
    //任务核心逻辑
    private final Consumer<T> taskHandler;

    private AsyncTaskManager(String workerNamePrefix, int maxWorkers, Consumer<T> taskHandler) {
        this.workerNamePrefix = workerNamePrefix;
        this.maxWorkers = maxWorkers;
        this.taskHandler = taskHandler;
        this.logger = LoggerFactory.getLogger(workerNamePrefix); //
    }

    @SuppressWarnings("unchecked")
    public static synchronized <T> AsyncTaskManager<T> getInstance(
            String name,
            int maxWorkers,
            Consumer<T> taskHandler
    ) {
        AsyncTaskManager<?> existing = INSTANCES.get(name.toUpperCase());
        if (existing != null) {
            return (AsyncTaskManager<T>) existing;
        }
        AsyncTaskManager<T> manager = new AsyncTaskManager<>(name, maxWorkers, taskHandler);
        INSTANCES.put(name.toUpperCase(), manager);
        return manager;
    }

    public void submitTask(List<T> tasks) {
        for (T task : tasks) {
            queue.offer(task);
        }
        startWorkerIfNeeded();
    }

    public void submitTask(T task) {
        queue.offer(task);
        startWorkerIfNeeded();
    }

    private void startWorkerIfNeeded() {
        // 按需启动，不超过 maxWorkers
        if (activeWorkerCount.get() < maxWorkers) {
            int newCount = activeWorkerCount.incrementAndGet();
            if (newCount <= maxWorkers) {
                CachedThreadPool.execute(new NeatLogicThread(workerNamePrefix + "-" + (newCount - 1), false) {
                    @Override
                    protected void execute() {
                        consumeQueue();
                    }
                });
            } else {
                activeWorkerCount.decrementAndGet();
            }
        }
    }

    private void consumeQueue() {
        try {
            while (true) {
                T task = queue.poll();
                if (task == null) {
                    break; // 队列空了就退出
                }
                try {
                    taskHandler.accept(task);
                } catch (ApiRuntimeException e) {
                    this.logger.warn(e.getMessage(), e);
                } catch (Exception e) {
                    this.logger.error(e.getMessage(), e);
                }
            }
        } finally {
            activeWorkerCount.decrementAndGet();
            // 如果队列还有任务，继续补充 worker
            if (!queue.isEmpty()) {
                startWorkerIfNeeded();
            }
        }
    }
}