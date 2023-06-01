package neatlogic.framework.message.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @Title: MessageHandlerNotFoundException
 * @Package neatlogic.framework.message.exception
 * @Description: 消息类型处理器不存在异常
 * @Author: linbq
 * @Date: 2020/12/31 15:19
 **/
public class MessageHandlerNotFoundException extends ApiRuntimeException {

    public MessageHandlerNotFoundException(String handler) {
        super("消息类型处理器：‘{0}’不存在", handler);
    }
}
