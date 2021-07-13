/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.mq.core;

import javax.jms.Session;
import javax.jms.TextMessage;

public interface ISubscribeHandler {
    String getName();

    void onMessage(TextMessage m, Session session, String topicName, String subscribeName, String tenantUuid);

    default String getClassName() {
        return this.getClass().getName();
    }
}
