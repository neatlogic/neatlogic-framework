package neatlogic.framework.exception.util;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @author longrf
 * @date 2022/3/7 3:38 下午
 */
public class StartTimeAndEndTimeCanNotFoundException extends ApiRuntimeException {
    public StartTimeAndEndTimeCanNotFoundException() {
        super("exception.framework.starttimeandendtimecannotfoundexception");
    }
}
