/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.dto.captcha;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

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
