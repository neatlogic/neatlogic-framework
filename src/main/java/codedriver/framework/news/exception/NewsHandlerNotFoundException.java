package codedriver.framework.news.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @Title: NewsHandlerNotFoundException
 * @Package codedriver.framework.news.exception
 * @Description: 消息类型处理器不存在异常
 * @Author: linbq
 * @Date: 2020/12/31 15:19
 **/
public class NewsHandlerNotFoundException extends ApiRuntimeException {

    public NewsHandlerNotFoundException(String handler) {
        super("消息类型处理器：‘" + handler+ "’不存在");
    }
}
