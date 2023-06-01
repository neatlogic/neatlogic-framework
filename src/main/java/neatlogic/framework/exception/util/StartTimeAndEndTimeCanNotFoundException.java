package neatlogic.framework.exception.util;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @author longrf
 * @date 2022/3/7 3:38 下午
 */
public class StartTimeAndEndTimeCanNotFoundException extends ApiRuntimeException {
    public StartTimeAndEndTimeCanNotFoundException() {
        super("请设定好开始时间和结束时间，使用参数startTime（开始时间）与endTime（结束时间）组合查询，或者使用timeRange（时间范围）与timeUnit（时间范围参数）组合查询");
    }
}
