/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.exception.captcha;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class LoginCaptchaIsEmptyException extends ApiRuntimeException {

    private static final long serialVersionUID = -6675900866180763840L;

    public LoginCaptchaIsEmptyException(int times) {
        super("登录错误次数超过{0}次，请输入验证码后重试", times);
    }

}
