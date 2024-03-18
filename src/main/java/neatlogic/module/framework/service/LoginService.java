/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
import neatlogic.framework.dto.UserVo;

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
}
