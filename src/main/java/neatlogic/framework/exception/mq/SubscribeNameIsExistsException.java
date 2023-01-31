/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.mq;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class SubscribeNameIsExistsException extends ApiRuntimeException {
    public SubscribeNameIsExistsException(String name) {
        super("订阅：" + name + "已存在，请使用新的唯一标识");
    }
}
