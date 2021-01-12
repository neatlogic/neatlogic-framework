package codedriver.framework.message.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @Title: MessageHandlerNotFoundException
 * @Package codedriver.framework.message.exception
 * @Description: 消息类型处理器不存在异常
 * @Author: linbq
 * @Date: 2020/12/31 15:19
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class MessageHandlerNotFoundException extends ApiRuntimeException {

    public MessageHandlerNotFoundException(String handler) {
        super("消息类型处理器：‘" + handler+ "’不存在");
    }
}
