/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.service;

import neatlogic.framework.dto.UserVo;
import com.alibaba.fastjson.JSONObject;

public interface LoginService {
    /**
     * 通过账号密码校验用户
     *
     * @param userParam 登录入参
     * @param resultJson 目前用于告知前端是否需要重新获取验证码，此处如果登录成功，则设置无需验证码
     * @return 合法用户
     */
    UserVo loginWithUserIdAndPassword(UserVo userParam, JSONObject resultJson);

    /**
     * 验证码验证是否合法
     *
     * @param jsonObj 入参
     * @param resultJson 目前用于告知前端是否需要重新获取验证码，此处如果登录失败超过制定次数，则设置需要验证码
     */
    void loginCaptchaValid(JSONObject jsonObj, JSONObject resultJson);

    /**
     * 获取验证码
     *
     * @return 验证码text 和 验证码base64图片
     */
    JSONObject getCaptcha();
}
