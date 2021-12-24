/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.service;

import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.LoginMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserVo;
import codedriver.framework.dto.captcha.LoginCaptchaVo;
import codedriver.framework.dto.captcha.LoginFailedCountVo;
import codedriver.framework.exception.captcha.LoginCaptchaIsEmptyException;
import codedriver.framework.exception.captcha.LoginCaptchaNotInvalidException;
import codedriver.framework.util.TimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.awt.*;
import java.util.Date;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    UserMapper userMapper;
    @Resource
    LoginMapper loginMapper;

    @Override
    public UserVo loginWithUserIdAndPassword(UserVo userParam){
        UserVo checkUserVo = userMapper.getUserByUserIdAndPassword(userParam);
        LoginFailedCountVo loginFailedCountVo = new LoginFailedCountVo();
        if(checkUserVo == null){//如果正常用户登录失败则，失败次数+1
            int failedCount = 1;
            loginFailedCountVo = loginMapper.getLoginFailedCountVoByUserId(userParam.getUserId());
            if(loginFailedCountVo != null){
                failedCount = loginFailedCountVo.getFailedCount();
            }
            loginFailedCountVo = new LoginFailedCountVo(userParam.getUserId(),failedCount);
            loginMapper.updateLoginFailedCount(loginFailedCountVo);
        }else{//如果正常用户登录成功，则清空该用户的失败次数
            loginMapper.deleteLoginFailedCountByUserId(userParam.getUserId());
        }
        return checkUserVo;
    }

    @Override
    public void loginCaptchaValid(JSONObject jsonObj) {
        //如果错误次数超多限制 则需要输入验证码
        Integer loginFailCount = loginMapper.getLoginFailedCountByUserId(jsonObj.getString("userid"));
        if (loginFailCount != null && loginFailCount >= Config.LOGIN_FAILED_TIMES_CAPTCHA()) {
            String sessionId = jsonObj.getString("sessionId");
            String code = jsonObj.getString("code");
            //校验验证码是否合法
            if (StringUtils.isNotBlank(sessionId) && StringUtils.isNotBlank(code)) {
                LoginCaptchaVo loginCaptchaVo = loginMapper.getLoginCaptchaBySessionId(sessionId);
                //code相等 且 没有超时
                if (loginCaptchaVo != null && Objects.equals(loginCaptchaVo.getCode(), code) && TimeUtil.compareDate(loginCaptchaVo.getExpiredTime(), new Date(System.currentTimeMillis()))) {
                    loginMapper.deleteLoginCaptchaBySessionId(sessionId);
                }else{
                    long expiredTime = System.currentTimeMillis() + Config.LOGIN_CAPTCHA_EXPIRED_TIME() * 1000L;
                    JSONObject result = getCaptcha();
                    loginMapper.updateLoginCaptcha(new LoginCaptchaVo(sessionId, result.getString("code"), new Date(expiredTime)));
                    throw new LoginCaptchaNotInvalidException();
                }
            } else {
                throw new LoginCaptchaIsEmptyException(Config.LOGIN_FAILED_TIMES_CAPTCHA());
            }
        }
    }


    /**
     * 获取验证码
     *
     * @return 验证码text 和 验证码base64图片
     */
    @Override
    public JSONObject getCaptcha() {
        JSONObject specCaptchaJson = new JSONObject();
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_NUM_AND_UPPER);
        specCaptchaJson.put("code", specCaptcha.text());
        specCaptchaJson.put("img", specCaptcha.toBase64());
        return specCaptchaJson;
    }
}
