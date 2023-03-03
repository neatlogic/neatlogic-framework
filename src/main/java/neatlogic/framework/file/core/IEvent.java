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
