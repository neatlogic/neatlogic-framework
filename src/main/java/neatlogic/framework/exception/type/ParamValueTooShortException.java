package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ParamValueTooShortException extends ApiRuntimeException {

    private static final long serialVersionUID = 327233755333422456L;

    public ParamValueTooShortException(String paramName, int valueLength, int maxLength) {
        super("参数：“{0}”允许最小长度是{2}个字符", paramName, maxLength);
    }
}
