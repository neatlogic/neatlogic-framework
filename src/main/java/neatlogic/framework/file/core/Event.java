/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.file.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.file.core.appender.Appender;
import neatlogic.framework.file.core.appender.AppenderManager;
import com.alibaba.fastjson.JSONObject;

import java.util.function.Consumer;

/**
 * 日志事件的内部表示形式。当做出记录的肯定决定时，将创建一个Event实例。这个实例被传递给不同的组件。
 *
 * 经典组件（如appenders）的编写者应该知道，某些Event字段是延迟初始化的。因此，希望输出数据以供接收器稍后正确读取的附加程序必须在写出“惰性”字段之前对其进行初始化。
 * 有关确切列表，请参阅{@link#prepareForDeferredProcessing()}方法。
 */
public class Event extends NeatLogicThread implements IEvent {
    /**
     * 生成此日志记录事件的线程的名称。
     */
    private String threadName;

    private String name;

    private String message;

    // 通过将formattedMessage标记为transient并在getFormattedMessage()中惰性地构造它，我们在序列化时获得了大量空间
    transient String formattedMessage;

    private long timeStamp;

    private Consumer preProcessor;

    private Consumer postProcessor;

    private final JSONObject data;

    private long beforeAppendFileSize;

    private IAuditType auditType;

    public Event(String name, long timeStamp, JSONObject data, Consumer preProcessor, Consumer postProcessor, IAuditType auditType) {
        super("Event");
        this.name = name;
        this.data = data;
        this.preProcessor = preProcessor;
        this.postProcessor = postProcessor;
        this.timeStamp = timeStamp;
        this.auditType = auditType;
        this.threadName = (Thread.currentThread()).getName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void execute() {
        Appender<IEvent> appender = AppenderManager.getAppender(this.auditType);
        appender.doAppend(this);
    }

    @Override
    public String getThreadName() {
        if (threadName == null) {
            threadName = (Thread.currentThread()).getName();
        }
        return threadName;
    }

    /**
     * 应在序列化事件之前调用此方法。当使用异步或延迟日志记录时，也应该调用它。
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
    public String toString() {
        return getFormattedMessage();
    }
}
