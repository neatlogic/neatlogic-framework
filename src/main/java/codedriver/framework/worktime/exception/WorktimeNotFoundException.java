/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.worktime.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class WorktimeNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 63938639408504619L;

    public WorktimeNotFoundException(String msg) {
        super("服务窗口:'" + msg + "'不存在");
    }
}
