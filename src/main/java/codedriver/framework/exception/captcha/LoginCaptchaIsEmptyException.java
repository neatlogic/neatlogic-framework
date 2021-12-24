/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.captcha;

import codedriver.framework.exception.core.ApiRuntimeException;

public class LoginCaptchaIsEmptyException extends ApiRuntimeException {

    private static final long serialVersionUID = -6675900866180763840L;

    public LoginCaptchaIsEmptyException(int times) {
        super("登录错误次数超过"+times+"次，请输入验证码后重试");
    }

}
