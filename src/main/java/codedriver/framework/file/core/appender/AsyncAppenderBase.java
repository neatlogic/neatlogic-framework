/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.appender;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.file.core.util.InterruptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 此基类和派生类异步记录事件。为了避免日志事件丢失，应该关闭此追加器。用户有责任关闭追加器，通常在应用程序生命周期结束时。
 *
 * 此追加器在{@link BlockingQueue}中缓冲事件。此追加器创建的{@link AsyncAppenderBase.Worker}线程从队列头获取事件，并将其分派给附加到此appender的单个appender。
 *
 * @param <E>
 */
public class AsyncAppenderBase<E> extends UnsynchronizedAppenderBase<E> {// implements AppenderAttachable<E>
    private Logger logger = LoggerFactory.getLogger(AsyncAppenderBase.class);
//    AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();
    private Appender appender;
    BlockingQueue<E> blockingQueue;

    /**
     * 默认缓冲区大小。
     */
    public static final int DEFAULT_QUEUE_SIZE = 256;
    int queueSize = DEFAULT_QUEUE_SIZE;

    static final int UNDEFINED = -1;
    /**
     *
     */
    int discardingThreshold = UNDEFINED;
    boolean neverBlock = false;

    AsyncAppenderBase.Worker worker = new AsyncAppenderBase.Worker(TenantContext.get().getTenantUuid());

    /**
     * 追加器停止期间允许的默认最大队列刷新时间。如果工作线程花费的时间超过此时间，它将退出，丢弃队列中的任何剩余项
     */
    public static final int DEFAULT_MAX_FLUSH_TIME = 1000;
    int maxFlushTime = DEFAULT_MAX_FLUSH_TIME;

    /**
     * 作为参数传递的eventObject是否可丢弃？该方法的基类实现总是返回“false”，但子类可能（并且确实）重写该方法。
     * 请注意，只有当缓冲区接近满时，才会丢弃事件。否则，当缓冲区“未满”时，会记录所有事件。
     *
     * @param eventObject
     * @return - 如果事件可以丢弃，则为false，否则为false
     *
     */
    protected boolean isDiscardable(E eventObject) {
        return false;
    }

    /**
     * 在排队之前对事件进行预处理。基类不进行预处理，但子类可以覆盖此行为。
     * @param eventObject
     */
    protected void preprocess(E eventObject) {
    }

    @Override
    public void start() {
        if (isStarted())
            return;
        if (appender == null) {
            logger.error("找不到附加的追加器。");
            return;
        }
        if (queueSize < 1) {
            logger.error("队列大小[" + queueSize + "]无效");
            return;
        }
        blockingQueue = new ArrayBlockingQueue<E>(queueSize);

        if (discardingThreshold == UNDEFINED) {
            discardingThreshold = queueSize / 5;
        }
        worker.setDaemon(true);
        worker.setName("AsyncAppender-Worker-" + getName());
        // 在启动工作线程之前，请确保此实例标记为“started”
        super.start();
        worker.start();
    }

    @Override
    public void stop() {
        if (!isStarted()) {
            return;
        }

        // 将此appender标记为stopped，以便Worker在调用aii.appendLoopOnAppenders时也可以处理PriorToRemoval，而子appender使用中断
        super.stop();

        // 中断工作线程，以便它可以终止。请注意，子追加器可以使用中断
        worker.interrupt();
        InterruptUtil interruptUtil = new InterruptUtil();

        try {
            interruptUtil.maskInterruptFlag();

            worker.join(maxFlushTime);

            // 检查线程是否结束，如果没有，则添加警告消息
            if (worker.isAlive()) {
                logger.warn("超过了最大队列刷新超时（" + maxFlushTime + " ms）。大约有10个排队事件可能被丢弃。");
            }
        } catch (InterruptedException e) {
            int remaining = blockingQueue.size();
            logger.error("无法加入工作线程。" + remaining + "个排队事件可能会被丢弃。");
            logger.error(e.getMessage(), e);
        } finally {
            interruptUtil.unmaskInterruptFlag();
        }
    }





    @Override
    protected void append(E eventObject) {
        if (isQueueBelowDiscardingThreshold() && isDiscardable(eventObject)) {
            return;
        }
        preprocess(eventObject);
        put(eventObject);
    }

    private boolean isQueueBelowDiscardingThreshold() {
        return (blockingQueue.remainingCapacity() < discardingThreshold);
    }

    private void put(E eventObject) {
        if (neverBlock) {
            blockingQueue.offer(eventObject);
        } else {
            putUninterruptibly(eventObject);
        }
    }

    private void putUninterruptibly(E eventObject) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    blockingQueue.put(eventObject);
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getDiscardingThreshold() {
        return discardingThreshold;
    }

    public void setDiscardingThreshold(int discardingThreshold) {
        this.discardingThreshold = discardingThreshold;
    }

    public int getMaxFlushTime() {
        return maxFlushTime;
    }

    public void setMaxFlushTime(int maxFlushTime) {
        this.maxFlushTime = maxFlushTime;
    }

    /**
     * 返回阻塞队列中当前的元素数。
     *
     * @return 队列中当前元素的数量。
     */
    public int getNumberOfElementsInQueue() {
        return blockingQueue.size();
    }

    public void setNeverBlock(boolean neverBlock) {
        this.neverBlock = neverBlock;
    }

    public boolean isNeverBlock() {
        return neverBlock;
    }

    /**
     * 阻塞队列中的剩余可用容量。
     *
     * @return 剩余容量
     * @see {@link BlockingQueue#remainingCapacity()}
     */
    public int getRemainingCapacity() {
        return blockingQueue.remainingCapacity();
    }

    public Appender getAppender() {
        return appender;
    }

    public void setAppender(Appender appender) {
        this.appender = appender;
    }
    //    public void addAppender(Appender<E> newAppender) {
//        if (appenderCount == 0) {
//            appenderCount++;
//            aai.addAppender(newAppender);
//        } else {
//            logger.warn("只能有一个appender附加到AsyncAppender。");
//            logger.warn("忽略名为[" + newAppender.getName() + "]的附加程序");
//        }
//    }
//
//    public Iterator<Appender<E>> iteratorForAppenders() {
//        return aai.iteratorForAppenders();
//    }
//
//    public Appender<E> getAppender(String name) {
//        return aai.getAppender(name);
//    }
//
//    public boolean isAttached(Appender<E> eAppender) {
//        return aai.isAttached(eAppender);
//    }
//
//    public void detachAndStopAllAppenders() {
//        aai.detachAndStopAllAppenders();
//    }
//
//    public boolean detachAppender(Appender<E> eAppender) {
//        return aai.detachAppender(eAppender);
//    }
//
//    public boolean detachAppender(String name) {
//        return aai.detachAppender(name);
//    }

    class Worker extends Thread {

        private String tenantUuid;

        public Worker(String tenantUuid) {
            this.tenantUuid = tenantUuid;
        }
        public void run() {
            TenantContext.init(tenantUuid);
            AsyncAppenderBase<E> parent = AsyncAppenderBase.this;
//            AppenderAttachableImpl<E> aai = parent.aai;

            // 在父级启动时循环
            while (parent.isStarted()) {
                try {
                    E e = parent.blockingQueue.take();
                    appender.doAppend(e);
//                    aai.appendLoopOnAppenders(e);
                } catch (InterruptedException ie) {
                    break;
                }
            }

            logger.info("工作线程将在退出之前刷新剩余事件。");
            for (E e : parent.blockingQueue) {
                appender.doAppend(e);
//                aai.appendLoopOnAppenders(e);
                parent.blockingQueue.remove(e);
            }

//            aai.detachAndStopAllAppenders();
        }
    }
}
