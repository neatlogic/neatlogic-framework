package neatlogic.framework.exception.util;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class FreemarkerTransformException extends ApiRuntimeException {
    private static final long serialVersionUID = 3623906073721231804L;

    public FreemarkerTransformException(String message) {
        super("模板转换失败，异常：{0}", message);
    }
}
