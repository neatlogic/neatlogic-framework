/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

import com.alibaba.fastjson.JSONObject;

import java.util.function.Consumer;

/**
 * 日志事件的内部表示形式。当做出记录的肯定决定时，将创建一个<code>LoggingEvent</code>实例。这个实例被传递给不同的logback经典组件。
 *
 * logback经典组件（如appenders）的编写者应该知道，某些LoggingEvent字段是延迟初始化的。因此，希望输出数据以供接收器稍后正确读取的附加程序必须在写出“惰性”字段之前对其进行初始化。
 * 有关确切列表，请参阅{@link#prepareForDeferredProcessing（）}方法。
 */
public class Event implements IEvent {
    /**
     * 生成此日志记录事件的线程的名称。
     */
    private String threadName;

    private String name;

    private String message;

    private String finalMessage;

    // 通过将formattedMessage标记为transient并在getFormattedMessage()中惰性地构造它，我们在序列化时获得了大量空间
    transient String formattedMessage;

    private long timeStamp;

    private Consumer preProcessor;

    private Consumer postProcessor;

    private final JSONObject data;

    private long beforeAppendFileSize;

    private boolean rollover = false;

    public Event(String name, long timeStamp, JSONObject data, Consumer preProcessor, Consumer postProcessor) {
        this.name = name;
        this.data = data;
        this.preProcessor = preProcessor;
        this.postProcessor = postProcessor;
        this.timeStamp = timeStamp;
    }
    public Event(String name, JSONObject data, Consumer preProcessor, Consumer postProcessor) {
        this(name, System.currentTimeMillis(), data, preProcessor, postProcessor);
    }

    public Event(String name, String message) {
        this.data = new JSONObject();
        this.name = name;
        this.message = message;

        timeStamp = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getThreadName() {
        if (threadName == null) {
            threadName = (Thread.currentThread()).getName();
        }
        return threadName;
    }

    public void setThreadName(String threadName) throws IllegalStateException {
        if (this.threadName != null) {
            throw new IllegalStateException("threadName has been already set");
        }
        this.threadName = threadName;
    }

    /**
     * 应在序列化事件之前调用此方法。当使用异步或延迟日志记录时，也应该调用它。
     * <p/>
     * <p/>
     * 注意，由于性能问题，此方法不提取调用方数据。呼叫者有责任提取呼叫者信息。
     */
    @Override
    public void prepareForDeferredProcessing() {
        this.getFormattedMessage();
        this.getThreadName();
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void setFinalMessage(String finalMessage) {
        this.finalMessage = finalMessage;
    }

    @Override
    public String getFinalMessage() {
        return this.finalMessage;
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String getFormattedMessage() {
        if (formattedMessage != null) {
            return formattedMessage;
        }
        formattedMessage = message;
        return formattedMessage;
    }

    @Override
    public void preProcessor() {
        preProcessor.accept(this);
    }

    @Override
    public void postProcessor() {
        postProcessor.accept(this);
    }

    @Override
    public JSONObject getData() {
        return data;
    }

    @Override
    public long getBeforeAppendFileSize() {
        return this.beforeAppendFileSize;
    }

    @Override
    public void setBeforeAppendFileSize(long fileSize) {
        this.beforeAppendFileSize = fileSize;
    }

    @Override
    public boolean isRollover() {
        return this.rollover;
    }

    @Override
    public void setRollover(boolean rollover) {
        this.rollover = rollover;
    }

    @Override
    public String toString() {
        return getFormattedMessage();
    }
}
