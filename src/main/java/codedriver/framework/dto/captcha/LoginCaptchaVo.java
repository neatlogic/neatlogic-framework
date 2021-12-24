/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.captcha;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

import java.util.Date;

public class LoginCaptchaVo {
    @EntityField(name = "会话id", type = ApiParamType.STRING)
    private String sessionId;
    @EntityField(name = "code", type = ApiParamType.STRING)
    private String code;
    @EntityField(name = "分组uuid", type = ApiParamType.LONG)
    private Date expiredTime;

    public LoginCaptchaVo(){}

    public LoginCaptchaVo(String sessionId, String code, Date expiredTime) {
        this.sessionId = sessionId;
        this.code = code;
        this.expiredTime = expiredTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }
}
