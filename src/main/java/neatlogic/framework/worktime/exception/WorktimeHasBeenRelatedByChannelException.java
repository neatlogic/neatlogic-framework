/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.worktime.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class WorktimeHasBeenRelatedByChannelException extends ApiRuntimeException {

    private static final long serialVersionUID = -3889598914385553027L;

    public WorktimeHasBeenRelatedByChannelException(String name) {
        super("服务窗口:'" + name + "'已被服务引用");
    }
}
