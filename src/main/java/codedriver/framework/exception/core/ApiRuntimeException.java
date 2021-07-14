/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.core;

import codedriver.framework.asynchronization.threadlocal.RequestContext;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
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
        super((TenantContext.get() != null ? TenantContext.get().getTenantUuid() : "") + ":" + (RequestContext.get() != null ? RequestContext.get().getUrl() : "") + ":::::::" + message, ex);
    }

    public ApiRuntimeException(String message) {
        super((TenantContext.get() != null ? TenantContext.get().getTenantUuid() : "") + ":" + (RequestContext.get() != null ? RequestContext.get().getUrl() : "") + ":::::::" + message);
    }

    public ApiRuntimeException(String message, JSONObject param) {
        super((TenantContext.get() != null ? TenantContext.get().getTenantUuid() : "") + ":" + (RequestContext.get() != null ? RequestContext.get().getUrl() : "") + ":::::::" + message);
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

    public String getMessage(boolean clear) {
        String message = super.getMessage();
        if (message.contains(":::::::")) {
            return message.split(":::::::")[1];
        } else {
            return message;
        }
    }

}
