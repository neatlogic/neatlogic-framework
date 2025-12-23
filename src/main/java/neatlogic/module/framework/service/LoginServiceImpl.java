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
import neatlogic.framework.config.ConfigManager;
import neatlogic.framework.config.FrameworkTenantConfig;
import neatlogic.framework.dao.mapper.LoginMapper;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.dto.captcha.LoginCaptchaVo;
import neatlogic.framework.dto.captcha.LoginFailedCountVo;
import neatlogic.framework.exception.captcha.LoginCaptchaIsEmptyException;
import neatlogic.framework.exception.captcha.LoginCaptchaNotInvalidException;
import neatlogic.framework.exception.user.LoginLockedException;
import neatlogic.framework.transaction.util.TransactionUtil;
import neatlogic.framework.util.CaptchaUtil;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    LoginMapper loginMapper;

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

    @Override
    public void updateFailCount(UserVo userVo, JSONObject resultJson, UserVo checkUserVo) {
        TransactionStatus tx = null;
        try {
            tx = TransactionUtil.openTx();
            LoginFailedCountVo loginFailedCountVo = loginMapper.getLoginFailedCountLockByUserId(userVo.getUserId());
            if (checkUserVo == null) {//如果正常用户登录失败则，失败次数+1
                int failedCount = 1;
                Date lockedUtil = null;
                if (loginFailedCountVo != null) {
                    if (loginFailedCountVo.getFailedCount() + 1 > Integer.parseInt(ConfigManager.getConfig(FrameworkTenantConfig.LOGIN_LOCKED_FAILED_COUNT))) {
                        lockedUtil = Date.from(
                                Instant.now().plus(Integer.parseInt(ConfigManager.getConfig(FrameworkTenantConfig.LOGIN_LOCKED_TIME)), ChronoUnit.MINUTES)
                        );
                    }
                    failedCount = loginFailedCountVo.getFailedCount();
                }
                Date lastFailedTime = Date.from(Instant.now());
                loginFailedCountVo = new LoginFailedCountVo(userVo.getUserId(), failedCount + 1, lockedUtil, lastFailedTime);
                loginMapper.updateLoginFailedCount(loginFailedCountVo);
            } else {//如果正常用户登录成功，则清空该用户的失败次数
                resultJson.remove("isNeedCaptcha");
                loginMapper.deleteLoginFailedCountByUserId(userVo.getUserId());
            }
            TransactionUtil.commitTx(tx);
        } catch (Exception ex) {
            if (tx != null) {
                TransactionUtil.rollbackTx(tx);
            }
            throw ex;
        }
    }

    @Override
    public void checkLockUser(UserVo paramUser) {
        if (Integer.parseInt(ConfigManager.getConfig(FrameworkTenantConfig.LOGIN_NEED_LOCK)) == 1) {
            TransactionStatus tx = null;
            try {
                tx = TransactionUtil.openTx();
                LoginFailedCountVo loginFailedCountVo = loginMapper.getLoginFailedCountLockByUserId(paramUser.getUserId());
                if (loginFailedCountVo != null && loginFailedCountVo.getLockedUntil() != null && new Date().before(loginFailedCountVo.getLockedUntil())) {
                    int failedCountLimit = Integer.parseInt(ConfigManager.getConfig(FrameworkTenantConfig.LOGIN_LOCKED_FAILED_COUNT));
                    String lockUtil = TimeUtil.convertDateToString(loginFailedCountVo.getLockedUntil(),TimeUtil.YYYY_MM_DD_HH_MM_SS);
                    throw new LoginLockedException(failedCountLimit, lockUtil);
                }
                TransactionUtil.commitTx(tx);
            } catch (Exception ex) {
                if (tx != null) {
                    TransactionUtil.rollbackTx(tx);
                }
                throw ex;
            }
        }
    }
}
