/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
