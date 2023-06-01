package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ParamValueTooLongException extends ApiRuntimeException {
    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 5528197166107887380L;

    public ParamValueTooLongException(String paramName, int valueLength, int maxLength) {
        super("参数：“{0}”允许最大长度是{2}个字符，当前长度：{1}", paramName, maxLength, valueLength);
    }
}
