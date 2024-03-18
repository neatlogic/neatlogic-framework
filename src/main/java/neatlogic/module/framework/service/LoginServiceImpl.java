/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.service;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dao.mapper.LoginMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.dto.captcha.LoginCaptchaVo;
import neatlogic.framework.dto.captcha.LoginFailedCountVo;
import neatlogic.framework.exception.captcha.LoginCaptchaIsEmptyException;
import neatlogic.framework.exception.captcha.LoginCaptchaNotInvalidException;
import neatlogic.framework.util.CaptchaUtil;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    UserMapper userMapper;
    @Resource
    LoginMapper loginMapper;

    @Override
    public UserVo loginWithUserIdAndPassword(UserVo userParam, JSONObject resultJson) {
        UserVo checkUserVo = userMapper.getUserByUserIdAndPassword(userParam);
        LoginFailedCountVo loginFailedCountVo = new LoginFailedCountVo();
        if (checkUserVo == null) {//如果正常用户登录失败则，失败次数+1
            int failedCount = 1;
            loginFailedCountVo = loginMapper.getLoginFailedCountVoByUserId(userParam.getUserId());
            if (loginFailedCountVo != null) {
                failedCount = loginFailedCountVo.getFailedCount();
            }
            loginFailedCountVo = new LoginFailedCountVo(userParam.getUserId(), failedCount);
            loginMapper.updateLoginFailedCount(loginFailedCountVo);
        } else {//如果正常用户登录成功，则清空该用户的失败次数
            resultJson.remove("isNeedCaptcha");
            loginMapper.deleteLoginFailedCountByUserId(userParam.getUserId());
        }
        return checkUserVo;
    }

    @Override
    public void loginCaptchaValid(JSONObject jsonObj, JSONObject resultJson) {
        //如果错误次数超多限制 则需要输入验证码
        Integer loginFailCount = loginMapper.getLoginFailedCountByUserId(jsonObj.getString("userid"));
        if (loginFailCount != null && loginFailCount >= Config.LOGIN_FAILED_TIMES_CAPTCHA()) {
            resultJson.put("isNeedCaptcha", 1);
            String sessionId = jsonObj.getString("sessionId");
            String code = jsonObj.getString("code");
            //校验验证码是否合法
            if (StringUtils.isNotBlank(sessionId) && StringUtils.isNotBlank(code)) {
                LoginCaptchaVo loginCaptchaVo = loginMapper.getLoginCaptchaBySessionId(sessionId);
                //code相等 且 没有超时
                code = code.toUpperCase(Locale.ROOT);
                if (loginCaptchaVo != null && Objects.equals(loginCaptchaVo.getCode(), code) && TimeUtil.compareDate(loginCaptchaVo.getExpiredTime(), new Date(System.currentTimeMillis()))) {
                    loginMapper.deleteLoginCaptchaBySessionId(sessionId);
                } else {
                    long expiredTime = System.currentTimeMillis() + Config.LOGIN_CAPTCHA_EXPIRED_TIME() * 1000L;
                    JSONObject result = CaptchaUtil.getCaptcha();
                    loginMapper.updateLoginCaptcha(new LoginCaptchaVo(sessionId, result.getString("code"), new Date(expiredTime)));
                    throw new LoginCaptchaNotInvalidException();
                }
            } else {
                throw new LoginCaptchaIsEmptyException(Config.LOGIN_FAILED_TIMES_CAPTCHA());
            }
        }
    }
}
