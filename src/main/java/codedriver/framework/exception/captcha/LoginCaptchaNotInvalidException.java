/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.captcha;

import codedriver.framework.exception.core.ApiRuntimeException;

public class LoginCaptchaNotInvalidException extends ApiRuntimeException {

    private static final long serialVersionUID = 4874681430601702912L;

    public LoginCaptchaNotInvalidException() {
        super("验证码不正确，请重新输入");
    }

}
