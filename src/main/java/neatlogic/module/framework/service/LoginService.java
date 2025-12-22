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

package neatlogic.module.framework.service;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dto.UserVo;

public interface LoginService {

    /**
     * 验证码验证是否合法
     *
     * @param jsonObj 入参
     * @param resultJson 目前用于告知前端是否需要重新获取验证码，此处如果登录失败超过制定次数，则设置需要验证码
     */
    void loginCaptchaValid(JSONObject jsonObj, JSONObject resultJson);

    /**
     *
     * @param userVo 用户入参
     * @param resultJson 返回值
     * @param checkUserVo 认证后的用户
     */
    void updateFailCount(UserVo userVo, JSONObject resultJson, UserVo checkUserVo);

    /**
     *
     * @param userVo 用户入参
     */
    void checkLockUser(UserVo userVo);
}
