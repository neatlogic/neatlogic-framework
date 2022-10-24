/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

import com.alibaba.fastjson.JSONObject;

/**
 * 事件接口
 */
public interface IEvent extends Runnable {
    /**
     * 线程名称
     * @return
     */
    String getThreadName();

    /**
     * 格式化后的消息
     * @return
     */
    String getFormattedMessage();

    String getName();

    /**
     * 事件发生时间点
     * @return
     */
    long getTimeStamp();

    /**
     * 准备延迟处理
     */
    void prepareForDeferredProcessing();

    /**
     * 消息
     * @param message
     */
    void setMessage(String message);

    /**
     * 追加写入前置处理器
     */
    void preProcessor();

    /**
     * 追加写入后置处理器
     */
    void postProcessor();

    /**
     * 数据
     * @return
     */
    JSONObject getData();

    /**
     * 追加写入前文件大小
     * @return
     */
    long getBeforeAppendFileSize();
    /**
     * 记录追加写入前文件大小
     */
    void setBeforeAppendFileSize(long fileSize);
}
