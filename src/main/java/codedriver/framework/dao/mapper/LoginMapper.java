/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.mapper;

import codedriver.framework.dto.captcha.LoginCaptchaVo;
import codedriver.framework.dto.captcha.LoginFailedCountVo;

public interface LoginMapper {

    LoginCaptchaVo getLoginCaptchaBySessionId(String key);

    Integer getLoginFailedCountByUserId(String userUuid);

    LoginFailedCountVo getLoginFailedCountVoByUserId(String userUuid);

    Integer updateLoginCaptcha(LoginCaptchaVo loginCaptchaVo);

    Integer updateLoginFailedCount(LoginFailedCountVo loginFailedCountVo);

    Integer deleteLoginCaptchaBySessionId(String sessionId);

    Integer deleteLoginFailedCountByUserId(String userId);
}
