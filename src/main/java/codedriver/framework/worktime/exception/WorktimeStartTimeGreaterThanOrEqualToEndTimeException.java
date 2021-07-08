/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.worktime.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @author linbq
 * @since 2021/7/7 16:24
 **/
public class WorktimeStartTimeGreaterThanOrEqualToEndTimeException extends ApiRuntimeException {

    private static final long serialVersionUID = -1703889762006158707L;

    public WorktimeStartTimeGreaterThanOrEqualToEndTimeException() {
        super("开始时间不能大于或等于结束时间");
    }
}
