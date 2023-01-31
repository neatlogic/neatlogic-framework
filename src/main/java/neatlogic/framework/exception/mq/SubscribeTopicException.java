/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.mq;

import neatlogic.framework.exception.core.ApiException;

public class SubscribeTopicException extends ApiException {
    public SubscribeTopicException(String topicName, String clientName, String error) {
        super(clientName + "订阅主题：" + topicName + "失败，异常：" + error);
    }
}
