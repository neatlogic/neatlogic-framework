package neatlogic.framework.exception.elasticsearch;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ElatsticSearchHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 8358695524151979636L;

    public ElatsticSearchHandlerNotFoundException(String handler) {
        super("找不到类型为：{0}的工单中心处理器", handler);
    }
}
