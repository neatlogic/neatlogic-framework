package neatlogic.framework.exception.integration;

import neatlogic.framework.exception.core.ApiRuntimeException;

/** 默认值错误同时包含参数路径和原因，便于定位数组单元格。 */
public class IntegrationDefaultValueInvalidException extends ApiRuntimeException {
    private static final long serialVersionUID = 1L;

    public IntegrationDefaultValueInvalidException(String path, String reason) {
        super("参数默认值配置错误：" + path + "，" + reason);
    }
}
