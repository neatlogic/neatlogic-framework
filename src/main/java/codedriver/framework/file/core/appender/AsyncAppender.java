/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.appender;

import codedriver.framework.file.core.IEvent;

public class AsyncAppender extends AsyncAppenderBase<IEvent> {
    /**
     * 如果队列满了，就丢弃当前事件
     */
    protected boolean isDiscardable(IEvent event) {
        return true;
    }

    /**
     * 在排队之前对事件进行预处理。
     * @param eventObject
     */
    protected void preprocess(IEvent eventObject) {
        eventObject.prepareForDeferredProcessing();
    }
}
