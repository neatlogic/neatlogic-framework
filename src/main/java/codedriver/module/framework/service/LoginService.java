/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.service;

import codedriver.framework.dto.UserVo;
import com.alibaba.fastjson.JSONObject;

public interface LoginService {
    /**
     * 通过账号密码校验用户
     *
     * @param userParam 登录入参
     * @return 合法用户
     */
    UserVo loginWithUserIdAndPassword(UserVo userParam);

    /**
     * 验证码验证是否合法
     *
     * @param jsonObj 入参
     */
    void loginCaptchaValid(JSONObject jsonObj, JSONObject resultJson);

    /**
     * 获取验证码
     *
     * @return 验证码text 和 验证码base64图片
     */
    JSONObject getCaptcha();
}
