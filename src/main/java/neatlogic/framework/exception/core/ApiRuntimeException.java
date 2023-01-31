/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.core;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import com.alibaba.fastjson.JSONObject;

public class ApiRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 9206337410118158624L;
    private String errorCode;

    private JSONObject param;

    public ApiRuntimeException() {
        super();
    }

    public ApiRuntimeException(Throwable ex) {
        super(ex);
    }

    public ApiRuntimeException(String message, Throwable ex) {
        super(message, ex);
    }

    public ApiRuntimeException(String message) {
        super(message);
    }

    public ApiRuntimeException(String message, JSONObject param) {
        super(message);
        this.param = param;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public JSONObject getParam() {
        return param;
    }

}
